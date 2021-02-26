package org.rowinson.healthcheck.adapters.handlers.service;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import org.rowinson.healthcheck.application.ServiceApplication;
import org.rowinson.healthcheck.framework.Http;
import org.rowinson.healthcheck.framework.verticles.PollerVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http handler for the removal of a service
 */
public class DeleteServiceHandler implements Handler<RoutingContext>{

  public static final Logger LOG = LoggerFactory.getLogger(GetServicesHandler.class);

  private ServiceApplication serviceApplication;
  private Vertx vertx;

  DeleteServiceHandler (Vertx vertx, ServiceApplication application) {
    this.vertx = vertx;
    this.serviceApplication = application;
  }

  @Override
  public void handle(RoutingContext context) {
    var userIdString = context.pathParam("userId");
    var serviceIdString = context.pathParam("serviceId");
    var userId = Long.parseLong(userIdString);
    var serviceId = Long.parseLong(serviceIdString);
    EventBus eb = vertx.eventBus();

    LOG.info("Creating service for userId {}", userId);

    serviceApplication.deleteServiceFromUser(userId, serviceId)
      .onFailure(Http.handleFailure(context, "Could not delete the service"))
      .onSuccess(next-> {
        LOG.info("Service deleted: {}", serviceId);

        eb.publish(PollerVerticle.MSG_SERVICE_DELETED, serviceId);

        context.response()
          .putHeader(Http.CONTENT_TYPE, Http.APPLICATION_JSON)
          .setStatusCode(200)
          .end();
      });
  }
}
