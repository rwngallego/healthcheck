package org.rowinson.healthcheck.framework.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.micrometer.PrometheusScrapingHandler;
import io.vertx.mysqlclient.MySQLPool;
import org.rowinson.healthcheck.adapters.handlers.service.ServiceApi;
import org.rowinson.healthcheck.adapters.handlers.user.UserApi;
import org.rowinson.healthcheck.framework.Config;
import org.rowinson.healthcheck.framework.Database;
import org.rowinson.healthcheck.framework.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configure the routers and start the HTTP server.
 * This verticle can be deployed with multiple instances.
 */
public class ApiServerVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final String PREFIX = "/api/v1";

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);

    Config.GetValues(vertx)
      .onSuccess(config -> {
        int port = config.getInteger(Config.WEB_PORT, 8080);

        // each RestApi verticle has 1 DB pool
        MySQLPool pool = Database.GetPool(vertx, config);

        // general handlers
        router.route().handler(BodyHandler.create());
        router.route().handler(Http.handleLogging());
        router.errorHandler(500, Http.handleRouterError());

        // attach the api handlers
        ServiceApi.attachHandlers(router, pool);
        UserApi.attachHandlers(router, pool);

        // register other routes
        router.route("/metrics").handler(PrometheusScrapingHandler.create());
        router.route("/*").handler(StaticHandler.create());

        // start the HTTP server
        vertx.createHttpServer().
          requestHandler(router).
          listen(port).
          onComplete(handleCompleteCreateHttpServer(startPromise, port));
      })
      .onFailure(error -> {
        LOG.error("Could not load the configuration {}", error);
        startPromise.fail(error);
      });
  }

  /**
   * Handle the completion of the HTTP server creation
   *
   * @param startPromise
   * @param port
   * @return
   */
  private Handler<AsyncResult<HttpServer>> handleCompleteCreateHttpServer(Promise<Void> startPromise, int port) {
    return http -> {
      if (http.succeeded()) {
        startPromise.complete();
        LOG.info("Http server started at port {}", port);
      } else {
        startPromise.fail(http.cause());
        LOG.error("Error: ", http.cause().toString());
      }
    };
  }
}
