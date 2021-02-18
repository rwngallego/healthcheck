package org.rowinson.healthcheck.application.commands;

/**
 * Command to update an existing service
 */
public class UpdateServiceCommand {
  private int userId;
  private int serviceId;
  private String name;
  private String url;

  public UpdateServiceCommand() {
  }

  public UpdateServiceCommand(int userId, int serviceId, String name, String url) {
    this.userId = userId;
    this.serviceId = serviceId;
    this.name = name;
    this.url = url;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getServiceId() {
    return serviceId;
  }

  public void setServiceId(int serviceId) {
    this.serviceId = serviceId;
  }
}
