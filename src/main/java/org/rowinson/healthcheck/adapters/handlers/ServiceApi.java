package org.rowinson.healthcheck.adapters.handlers;

import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;
import org.rowinson.healthcheck.adapters.handlers.service.GetServicesHandler;
import org.rowinson.healthcheck.adapters.repositories.MySQLServiceRepository;
import org.rowinson.healthcheck.adapters.verticles.ApiServerVerticle;
import org.rowinson.healthcheck.application.ServiceApplication;
import org.rowinson.healthcheck.application.interfaces.ServiceRepository;

/**
 * Registers all the HTTP handlers related to the services endpoints
 */
public class ServiceApi {
  public static void attachHandlers(Router router, MySQLPool pool) {
    ServiceRepository serviceRepository = new MySQLServiceRepository(pool);
    ServiceApplication serviceApplication = new ServiceApplication(serviceRepository);

    router.get(ApiServerVerticle.PREFIX + "/services").handler(new GetServicesHandler(serviceApplication));
  }
}
