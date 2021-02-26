package org.rowinson.healthcheck.framework;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the Http constant definitions helpers
 */
public class Http {
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String APPLICATION_JSON = "application/json";

  public static final Logger LOG = LoggerFactory.getLogger(Http.class);

  /**
   * Logs the incoming requests
   *
   * @return
   */
  public static Handler<RoutingContext> handleLogging() {
    return r -> {
      LOG.info("{} {} | Body: {}", r.request().method(), r.normalizedPath(), r.getBodyAsString());
      r.next();
    };
  }

  /**
   * Handle the errors occurred in the router
   *
   * @return
   */
  public static Handler<RoutingContext> handleRouterError() {
    return rc -> {
      Throwable failure = rc.failure();
      if (failure != null) {
        LOG.error("Error: {}", failure.toString());
      }
    };
  }

  /**
   * Handles all the HTTP failures and sends an HTTP response with a message
   *
   * @param context
   * @param message
   * @return
   */
  public static Handler<Throwable> handleFailure(RoutingContext context, String message) {
    return error -> {
      LOG.error("Error: ", error);

      context.response()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .putHeader(Http.CONTENT_TYPE, Http.APPLICATION_JSON)
        .end(new JsonObject()
          .put("message", message)
          .toBuffer()
        );
    };
  }
}
