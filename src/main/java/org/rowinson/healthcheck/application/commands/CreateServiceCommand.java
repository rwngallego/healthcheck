package org.rowinson.healthcheck.application.commands;

/**
 * Command to create a new service
 */
public class CreateServiceCommand {
  private int userId;
  private String name;
  private String url;

  public CreateServiceCommand(){ }

  public CreateServiceCommand(int userId, String name, String url) {
    this.userId = userId;
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
}
