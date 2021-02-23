package org.rowinson.healthcheck.application.interfaces;

import io.vertx.core.Future;
import org.rowinson.healthcheck.domain.Service;

import java.util.ArrayList;

/**
 * Interface for the DB access related to services
 */
public interface ServiceRepository {
  Future<ArrayList<Service>> getAllServices(Long userId, int offset, int size, String orderBy, String orderAsc);

  Future<Service> getService(Long userId, Long serviceId);

  Future<Long> createService(Service service);

  Future<Void> updateService(Service service);

  Future<Void> deleteService(Long serviceId);
}
