package org.rowinson.healthcheck.adapters.repositories;

import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.rowinson.healthcheck.application.interfaces.ServiceRepository;
import org.rowinson.healthcheck.domain.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the ServiceRepository and is in charge
 * of the DB interactions related to the Services.
 */
public class MySQLServiceRepository implements ServiceRepository {
  private static final Logger LOG = LoggerFactory.getLogger(MySQLServiceRepository.class);
  private MySQLPool pool;

  public MySQLServiceRepository(MySQLPool pool) {
    this.pool = pool;
  }

  /**
   * Get the paginated services. Order by and Order Asc/Desc is not implemented
   *
   * @param userId
   * @param offset
   * @param size
   * @param orderBy  not implemented
   * @param orderAsc not implemented
   * @return
   */
  @Override
  public Future<ArrayList<Service>> getPaginatedServices(Long userId, int offset, int size, String orderBy, String orderAsc) {
    LOG.info("Getting all the services from DB for userId: {}", userId);

    return SqlTemplate.forQuery(pool, "SELECT s.id, s.user_id, s.name, s.url, s.status, s.created_at, s.updated_at FROM services s WHERE s.user_id = #{userId}")
      .mapTo(Service.class)
      .execute(Collections.singletonMap("userId", userId))
      .compose(results -> {
        var services = new ArrayList<Service>();
        results.forEach(e -> services.add(e));

        LOG.debug("Retrieved: {}", services.toString());
        return Future.succeededFuture(services);
      });
  }

  /**
   * Get all the registered services
   * @return
   */
  @Override
  public Future<ArrayList<Service>> getAllServices() {
    LOG.info("Getting all the services from DB");

    return SqlTemplate.forQuery(pool, "SELECT s.id, s.user_id, s.name, s.url, s.status, s.created_at, s.updated_at FROM services s")
      .mapTo(Service.class)
      .execute(Collections.emptyMap())
      .compose(results -> {
        var services = new ArrayList<Service>();
        results.forEach(e -> services.add(e));

        LOG.debug("Retrieved: {}", services.toString());
        return Future.succeededFuture(services);
      });
  }

  /**
   * Get the service
   *
   * @param userId
   * @param serviceId
   * @return
   */
  @Override
  public Future<Service> getService(Long userId, Long serviceId) {
    LOG.info("Getting service from DB for userId: {}, service: {}", userId, serviceId);

    Map<String, Object> params = new HashMap<>();
    params.put("id", serviceId);
    params.put("userId", userId);
    return SqlTemplate.forQuery(pool,
      "SELECT s.id, s.user_id, s.name, s.url, s.status, s.created_at, s.updated_at FROM services s WHERE s.id = #{id} AND s.user_id = #{userId}")
      .mapTo(Service.class)
      .execute(params)
      .compose(results -> {
        var result = results.iterator().hasNext() ? results.iterator().next() : null;

        LOG.debug("Retrieved: {}", result);
        return Future.succeededFuture(result);
      });
  }

  /**
   * Create a new service
   *
   * @param service
   * @return
   */
  @Override
  public Future<Long> createService(Service service) {
    LOG.info("Creating service in DB: {}", service.toString());

    Map<String, Object> params = new HashMap<>();
    params.put("userId", service.getUserId());
    params.put("name", service.getName());
    params.put("url", service.getUrl());
    return SqlTemplate.forQuery(pool, "INSERT INTO services (user_id, name, url) VALUES (#{userId}, #{name}, #{url})")
      .execute(params)
      .compose(results -> {
        Long id = results.property(MySQLClient.LAST_INSERTED_ID);

        LOG.debug("Created: {}", id);
        return Future.succeededFuture(id);
      });
  }

  /**
   * Update the service
   *
   * @param service
   * @return
   */
  @Override
  public Future<Void> updateService(Service service) {
    LOG.info("Updating service in DB: {}", service.toString());

    Map<String, Object> params = new HashMap<>();
    params.put("serviceId", service.getId());
    params.put("name", service.getName());
    params.put("url", service.getUrl());
    return SqlTemplate.forUpdate(pool, "UPDATE services SET name=#{name}, url=#{url} WHERE id = #{serviceId}")
      .execute(params)
      .compose(results -> {
        LOG.debug("Service updated, service: {}", service.getId());
        return Future.succeededFuture();
      });
  }

  /**
   * Delete the service
   *
   * @param userId
   * @param serviceId
   * @return
   */
  @Override
  public Future<Void> deleteService(Long userId, Long serviceId) {
    LOG.info("Deleting service from DB: {}", serviceId);

    Map<String, Object> params = new HashMap<>();
    params.put("id", serviceId);
    params.put("userId", userId);
    return SqlTemplate.forUpdate(pool, "DELETE FROM services WHERE id = #{id} AND user_id = #{userId}")
      .execute(params)
      .compose(results -> {
        LOG.debug("Service deleted: {}", serviceId);
        return Future.succeededFuture();
      });
  }
}
