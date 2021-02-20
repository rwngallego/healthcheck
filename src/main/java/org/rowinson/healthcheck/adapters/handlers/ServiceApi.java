package org.rowinson.healthcheck.adapters.handlers;

import io.vertx.ext.web.Router;
import org.rowinson.healthcheck.adapters.handlers.service.GetServicesHandler;

public class ServiceApi {
  public static void attachHandlers(Router router) {
    router.get("/api/v1/services").handler(new GetServicesHandler());
  }
}
