package org.rowinson.healthcheck.adapters.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.micrometer.PrometheusScrapingHandler;
import io.vertx.mysqlclient.MySQLPool;
import org.rowinson.healthcheck.Config;
import org.rowinson.healthcheck.Database;
import org.rowinson.healthcheck.adapters.handlers.ServiceApi;
import org.rowinson.healthcheck.adapters.handlers.UserApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configure the routers and start the HTTP server.
 * This verticle can be deployed with multiple instances.
 */
public class ApiServerVerticle extends AbstractVerticle {

  public static final String PREFIX = "/api/v1";
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);

    Config.GetValues(vertx)
      .onSuccess(config -> {
        int port = config.getInteger(Config.WEB_PORT, 8080);

        // each RestApi verticle has 1 DB pool
        MySQLPool pool = Database.GetPool(vertx, config);

        // attach the handlers
        ServiceApi.attachHandlers(router, pool);
        UserApi.attachHandlers(router, pool);

        // register all the other routes
        router.route("/metrics").handler(PrometheusScrapingHandler.create());
        router.route("/*").handler(StaticHandler.create());

        // error handling
        router.errorHandler(500, handleErrorRouter());

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

  /**
   * Handle the errors occurred in the router
   *
   * @return
   */
  private Handler<RoutingContext> handleErrorRouter() {
    return rc -> {
      System.err.println("Handling failure");
      Throwable failure = rc.failure();
      if (failure != null) {
        failure.printStackTrace();
      }
    };
  }
}
