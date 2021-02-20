package org.rowinson.healthcheck.application.interfaces;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import org.rowinson.healthcheck.domain.Service;

public interface ServiceRepository {
  public Future<RowSet<Row>> GetAllServices(int offset, int size, String orderBy, String orderAsc);
  public Service GetService(int serviceId);
  public void CreateService(Service service);
  public void UpdateService(Service service);
  public void DeleteService(int serviceId);
}
