package org.rowinson.healthcheck.adapters.handlers.service;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.rowinson.healthcheck.framework.Http;
import org.rowinson.healthcheck.application.ServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetServicesHandler implements Handler<RoutingContext> {
  public static final Logger LOG = LoggerFactory.getLogger(GetServicesHandler.class);

  private ServiceApplication serviceApplication;

  public GetServicesHandler(ServiceApplication application) {
    this.serviceApplication = application;
  }

  @Override
  public void handle(RoutingContext context) {
    long userId = 1;
    serviceApplication.getBelongingServices(userId, 0, 10, "name", "asc")
      .onSuccess(services -> {
        JsonArray result = new JsonArray();
        services.stream().forEach(a -> result.add(a));

        LOG.info("{} | {}", context.normalizedPath(), result.encode());

        context.response()
          .putHeader(Http.CONTENT_TYPE, Http.APPLICATION_JSON)
          .end(result.toBuffer());
      })
    .onFailure(error -> {
      LOG.error("Could not get the belonging services: ", error);

      context.failure();
    });
  }
}
