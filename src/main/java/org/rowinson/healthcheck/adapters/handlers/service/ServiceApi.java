package org.rowinson.healthcheck.adapters.handlers.service;

import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;
import org.rowinson.healthcheck.adapters.repositories.MySQLServiceRepository;
import org.rowinson.healthcheck.application.ServiceApplication;
import org.rowinson.healthcheck.application.interfaces.ServiceRepository;
import org.rowinson.healthcheck.framework.verticles.ApiServerVerticle;

/**
 * Registers all the HTTP handlers related to the services endpoints
 */
public class ServiceApi {
  public static void attachHandlers(Router router, MySQLPool pool) {
    ServiceRepository serviceRepository = new MySQLServiceRepository(pool);
    ServiceApplication serviceApplication = new ServiceApplication(serviceRepository);

    router.get(ApiServerVerticle.PREFIX + "/users/:userId/services").handler(new GetServicesHandler(serviceApplication));
    router.post(ApiServerVerticle.PREFIX + "/users/:userId/services").handler(new CreateServiceHandler(serviceApplication));
  }
}
