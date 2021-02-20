package org.rowinson.healthcheck.adapters.repositories;

import io.vertx.mysqlclient.MySQLPool;
import org.rowinson.healthcheck.application.interfaces.ServiceRepository;
import org.rowinson.healthcheck.domain.Service;

import java.util.List;

/**
 * This class implements the ServiceRepository and is in charge
 * of handling the DB interactions related to the Services.
 */
public class MySQLServiceRepository implements ServiceRepository {
  private MySQLPool pool;

  public MySQLServiceRepository(MySQLPool pool) {
    pool = pool;
  }

  @Override
  public List<Service> GetAllServices(int offset, int size, String orderBy, String orderAsc) {
    return null;
  }

  @Override
  public Service GetService(int serviceId) {
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
