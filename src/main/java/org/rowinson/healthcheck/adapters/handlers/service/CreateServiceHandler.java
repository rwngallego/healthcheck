package org.rowinson.healthcheck.adapters.handlers.service;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.rowinson.healthcheck.application.ServiceApplication;
import org.rowinson.healthcheck.domain.Service;
import org.rowinson.healthcheck.framework.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateServiceHandler implements Handler<RoutingContext>{

  public static final Logger LOG = LoggerFactory.getLogger(GetServicesHandler.class);

  private ServiceApplication serviceApplication;

  CreateServiceHandler (ServiceApplication application) {
    this.serviceApplication = application;
  }

  @Override
  public void handle(RoutingContext context) {
    long userId = 1;
    var params = context.getBodyAsJson();
    var service = params.mapTo(Service.class);

    serviceApplication.addServiceToUser(userId, service)
      .onSuccess(serviceId -> {
        context.response()
          .putHeader(Http.CONTENT_TYPE, Http.APPLICATION_JSON)
          .setStatusCode(200)
          .end();
      })
      .onFailure(error -> {
        LOG.error("Could not create the service: ", error);
        context.failure();
      });
  }
}
