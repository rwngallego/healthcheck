package org.rowinson.healthcheck.application;

import io.vertx.core.Future;
import org.rowinson.healthcheck.application.interfaces.ServiceRepository;
import org.rowinson.healthcheck.domain.Service;

import java.util.ArrayList;

/**
 * Manages all the logic related to the service handling
 */
public class ServiceApplication {
  private ServiceRepository repo;

  /**
   * Constructor
   *
   * @param repo
   */
  public ServiceApplication(ServiceRepository repo) {
    this.repo = repo;
  }

  /**
   * Return the list of services that belong to the given user
   *
   * @param userId
   * @param offset
   * @param size
   * @param orderBy
   * @param orderAsc
   * @return
   */
  public Future<ArrayList<Service>> getBelongingServices(Long userId, int offset, int size, String orderBy, String orderAsc) {
    return repo.getPaginatedServices(userId, offset, size, orderBy, orderAsc);
  }

  /**
   * Return all the services
   *
   * @return
   */
  public Future<ArrayList<Service>> getRegisteredServices() {
    return repo.getAllServices();
  }

  /**
   * Creates a new service for the user
   *
   * @param userId
   * @param service
   * @return
   */
  public Future<Long> addServiceToUser(Long userId, Service service) {
    service.setUserId(userId);
    return repo.createService(service);
  }

  /**
   * Get a service from the user by the given service id
   *
   * @param userId
   * @param serviceId
   * @return
   */
  public Future<Service> getServiceById(Long userId, Long serviceId) {
    return repo.getService(userId, serviceId);
  }

  /**
   * Delete a service from the user
   *
   * @param userId
   * @param serviceId
   * @return
   */
  public Future<Void> deleteServiceFromUser(Long userId, Long serviceId) {
    return repo.deleteService(userId, serviceId);
  }

  /**
   * Changes the status of the service
   *
   * @param service
   * @return
   */
  public Future<Void> changeServiceStatus(Service service, String status) {
    service.setStatus(status);

    // For time constrains we're using this, but ideally in the
    // repository it should be just a single SQL query for a single value
    return repo.updateService(service);
  }
}
