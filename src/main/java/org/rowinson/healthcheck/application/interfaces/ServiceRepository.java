package org.rowinson.healthcheck.application.interfaces;

import io.vertx.core.Future;
import org.rowinson.healthcheck.domain.Service;

import java.util.ArrayList;

/**
 * Interface for the DB access related to services
 */
public interface ServiceRepository {
  Future<ArrayList<Service>> GetAllServices(Long userId, int offset, int size, String orderBy, String orderAsc);

  Future<Service> GetService(Long userId, Long serviceId);

  Future<Long> CreateService(Service service);

  Future<Void> UpdateService(Service service);

  Future<Void> DeleteService(Long serviceId);
}
