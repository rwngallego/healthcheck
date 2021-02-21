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
  private MySQLPool pool;

  private static final Logger LOG = LoggerFactory.getLogger(MySQLServiceRepository.class);

  public MySQLServiceRepository(MySQLPool pool) {
    this.pool = pool;
  }

  @Override
  public Future<ArrayList<Service>> GetAllServices(Long userId, int offset, int size, String orderBy, String orderAsc) {
    return SqlTemplate.forQuery(pool,"SELECT s.id, s.user_id, s.name, s.url, s.created_at, s.updated_at FROM services s WHERE s.user_id = #{userId}")
      .mapTo(Service.class)
      .execute(Collections.singletonMap("userId", userId))
      .compose(results -> {
        var services = new ArrayList<Service>();
        results.forEach(e -> services.add(e));
        return Future.succeededFuture(services);
      });
  }

  @Override
  public Future<Service> GetService(Long userId, Long serviceId) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", serviceId);
    params.put("userId", userId);
    return SqlTemplate.forQuery(pool,
      "SELECT s.id, s.user_id, s.name, s.url, s.created_at, s.updated_at FROM services s WHERE s.id = #{id} AND s.user_id = #{userId}")
      .mapTo(Service.class)
      .execute(params)
      .compose(results -> {
        var result = results.iterator().hasNext() ? results.iterator().next() : null;
        return Future.succeededFuture(result);
      });
  }

  @Override
  public Future<Long> CreateService(Service service) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", service.getUserId());
    params.put("name", service.getName());
    params.put("url", service.getUrl());
    return SqlTemplate.forQuery(pool, "INSERT INTO services (user_id, name, url) VALUES (#{userId}, #{name}, #{url})")
      .execute(params)
      .compose(results -> {
        Long id = results.property(MySQLClient.LAST_INSERTED_ID);
        return Future.succeededFuture(id);
      });
  }

  @Override
  public Future<Void> UpdateService(Service service) {
    Map<String, Object> params = new HashMap<>();
    params.put("serviceId", service.getId());
    params.put("name", service.getName());
    params.put("url", service.getUrl());
    return SqlTemplate.forUpdate(pool, "UPDATE services SET name=#{name}, url=#{url} WHERE id = #{serviceId}")
      .execute(params)
      .compose(results -> Future.succeededFuture());
  }

  @Override
  public Future<Void> DeleteService(Long serviceId) {
    return SqlTemplate.forUpdate(pool, "DELETE FROM services WHERE id = #{id}")
      .execute(Collections.singletonMap("id", serviceId))
      .compose(results -> Future.succeededFuture());
  }
}
