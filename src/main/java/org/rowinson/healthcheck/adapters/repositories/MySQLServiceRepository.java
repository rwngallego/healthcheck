package org.rowinson.healthcheck.adapters.repositories;

import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.rowinson.healthcheck.application.interfaces.ServiceRepository;
import org.rowinson.healthcheck.domain.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * This class implements the ServiceRepository and is in charge
 * of handling the DB interactions related to the Services.
 */
public class MySQLServiceRepository implements ServiceRepository {
  private MySQLPool pool;

  private static final Logger LOG = LoggerFactory.getLogger(MySQLServiceRepository.class);

  public MySQLServiceRepository(MySQLPool pool) {
    this.pool = pool;
  }

  @Override
  public Future<RowSet<Row>> GetAllServices(int offset, int size, String orderBy, String orderAsc) {
    return pool.query("SELECT s.name, s.url, s.created_at, s.updated_at FROM services s")
      .execute();
  }

  @Override
  public Service  GetService(int serviceId) {
    SqlTemplate.forQuery(pool,
      "SELECT s.name, s.url, s.created_at, s.updated_at FROM services")
      .execute(Collections.singletonMap("id", 1))
      .onFailure(error -> {
        LOG.error("Could not retrieve the services from the DB: {}", error);
      });
    return null;
  }

  @Override
  public void CreateService(Service service) {

  }

  @Override
  public void UpdateService(Service service) {

  }

  @Override
  public void DeleteService(int serviceId) {

  }
}
