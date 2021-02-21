package org.rowinson.healthcheck.application.interfaces;

import io.vertx.core.Future;
import org.rowinson.healthcheck.domain.Service;

import java.util.ArrayList;

/**
 * Interface for the DB access related to services
 */
public interface ServiceRepository {
  public Future<ArrayList<Service>> GetAllServices(Long userId, int offset, int size, String orderBy, String orderAsc);
  public Future<Service> GetService(Long userId, Long serviceId);
  public Future<Long> CreateService(Service service);
  public Future<Void> UpdateService(Service service);
  public Future<Void> DeleteService(Long serviceId);
}
