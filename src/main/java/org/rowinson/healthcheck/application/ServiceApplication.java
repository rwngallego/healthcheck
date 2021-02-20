package org.rowinson.healthcheck.application;

import org.rowinson.healthcheck.application.interfaces.ServiceRepository;

public class ServiceApplication {
  private ServiceRepository repo;

  public ServiceApplication(ServiceRepository repo) {
    repo = repo;
  }
}
