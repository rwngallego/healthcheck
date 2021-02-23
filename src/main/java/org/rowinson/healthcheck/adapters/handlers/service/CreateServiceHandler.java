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
    var userIdString = context.pathParam("userId");
    var userId = Long.parseLong(userIdString);
    var params = context.getBodyAsJson();
    var serviceToCreate = params.mapTo(Service.class);

    LOG.info("Creating service for userId {}", userId);

    serviceApplication.addServiceToUser(userId, serviceToCreate)
      .onFailure(Http.handleFailure(context, "Could not create the service"))
      .compose(serviceId -> serviceApplication.getServiceById(userId, serviceId))
      .onFailure(Http.handleFailure(context, "Could not retrieve the created service"))
      .onSuccess(service -> {
        LOG.info("Service created: {}", service);
        context.response()
          .putHeader(Http.CONTENT_TYPE, Http.APPLICATION_JSON)
          .setStatusCode(200)
          .end(service.toJson().toBuffer());
      });
  }
}
