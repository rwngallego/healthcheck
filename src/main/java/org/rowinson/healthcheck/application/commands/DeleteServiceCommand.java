package org.rowinson.healthcheck.application.commands;

/**
 * Command to delete a service
 */
public class DeleteServiceCommand {
  private int userId;
  private int serviceId;

  public DeleteServiceCommand() { }

  public DeleteServiceCommand(int userId, int serviceId) {
    this.userId = userId;
    this.serviceId = serviceId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getServiceId() {
    return serviceId;
  }

  public void setServiceId(int serviceId) {
    this.serviceId = serviceId;
  }
}
