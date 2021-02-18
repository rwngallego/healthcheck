package org.rowinson.healthcheck.application;

import org.rowinson.healthcheck.domain.Service;

import java.util.ArrayList;
import java.util.Date;

public class ServiceProjection {
  /**
   * Get all the services that we need to monitor
   * @return
   */
  public ArrayList<Service> GetServicesToMonitor() {
    Service service = new Service("message-bus", "http://127.0.0.1:2000", new Date(), new Date());

    ArrayList<Service> services = new ArrayList();
    services.add(service);
    return services;
  }
}
