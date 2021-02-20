package org.rowinson.healthcheck.adapters.verticles;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.micrometer.PrometheusScrapingHandler;
import org.rowinson.healthcheck.adapters.handlers.ServiceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configure and deploys the router, register
 * the routes and the http handlers
 */
public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);

    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig()
      .onSuccess(json -> {
        int port = json.getInteger("port", 8080);

        // attach all the application http handlers
        ServiceApi.attachHandlers(router);

        // register other routes
        router.route("/metrics").handler(PrometheusScrapingHandler.create());
        router.route("/*").handler(StaticHandler.create());

        // configure the router
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
