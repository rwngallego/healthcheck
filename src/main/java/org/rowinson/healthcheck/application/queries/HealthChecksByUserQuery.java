package org.rowinson.healthcheck.application.queries;

/**
 * Query to retrieve the health checks for the services
 * that belong to the user
 */
public class HealthChecksByUserQuery {
  private int userId;

  public HealthChecksByUserQuery(){ }

  public HealthChecksByUserQuery(int userId) {
   this.userId = userId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }
}
