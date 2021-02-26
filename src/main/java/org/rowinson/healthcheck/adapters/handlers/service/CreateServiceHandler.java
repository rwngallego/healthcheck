package org.rowinson.healthcheck.adapters.handlers.service;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import org.rowinson.healthcheck.application.ServiceApplication;
import org.rowinson.healthcheck.domain.Service;
import org.rowinson.healthcheck.framework.Http;
import org.rowinson.healthcheck.framework.verticles.PollerVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http handler for the creation of services
 */
public class CreateServiceHandler implements Handler<RoutingContext> {

  public static final Logger LOG = LoggerFactory.getLogger(GetServicesHandler.class);

  private Vertx vertx;
  private ServiceApplication serviceApplication;

  CreateServiceHandler(Vertx vertx, ServiceApplication application) {
    this.vertx = vertx;
    this.serviceApplication = application;
  }

  @Override
  public void handle(RoutingContext context) {
    var userIdString = context.pathParam("userId");
    var userId = Long.parseLong(userIdString);
    var params = context.getBodyAsJson();
    var serviceToCreate = params.mapTo(Service.class);
    EventBus eb = vertx.eventBus();

    LOG.info("Creating service for userId {}", userId);

    serviceApplication.addServiceToUser(userId, serviceToCreate)
      .onFailure(Http.handleFailure(context, "Could not create the service"))
      .compose(serviceId -> serviceApplication.getServiceById(userId, serviceId))
      .onFailure(Http.handleFailure(context, "Could not retrieve the created service"))
      .onSuccess(service -> {
        LOG.info("Service created: {}", service);

        eb.publish(PollerVerticle.MSG_SERVICE_CREATED, service.toJson());
        context.response()
          .putHeader(Http.CONTENT_TYPE, Http.APPLICATION_JSON)
          .setStatusCode(200)
          .end(service.toJson().toBuffer());
      });
  }
}
