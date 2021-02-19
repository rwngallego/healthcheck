package org.rowinson.healthcheck;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.micrometer.PrometheusScrapingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    JsonObject account = new JsonObject();
    account.put("name", "Account");
    account.put("url", "127.0.0.1:2001");
    JsonArray services = new JsonArray();
    services.add(account);

    Router router = Router.router(vertx);
    router.get("/api/v1/services").handler(context -> {
      context.response().putHeader("Content-Type", "application/json").end(services.toBuffer());
    });
//    router.get().handler(StaticHandler.create());
    router.route("/metrics").handler(PrometheusScrapingHandler.create());
    router.route("/*").handler(StaticHandler.create());
    router.errorHandler(500, rc -> {
      System.err.println("Handling failure");
      Throwable failure = rc.failure();
      if (failure != null) {
        failure.printStackTrace();
      }
    });

    vertx.exceptionHandler( e -> {
      LOG.error("Error occurred: {}", e.getStackTrace().toString());
    });
    vertx.createHttpServer().
      requestHandler(router).
      listen(8888).
     onComplete(http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port 8888");
        } else {
          LOG.error("Error: ", http.cause().toString());
          startPromise.fail(http.cause());
        }
    });
  }
}
