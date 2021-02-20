package org.rowinson.healthcheck.application.interfaces;

import org.rowinson.healthcheck.domain.Service;

import java.util.List;

public interface ServiceRepository {
  public List<Service> GetAllServices(int offset, int size, String orderBy, String orderAsc);
  public Service GetService(int serviceId);
  public void CreateService(Service service);
  public void UpdateService(Service service);
  public void DeleteService(int serviceId);
}
