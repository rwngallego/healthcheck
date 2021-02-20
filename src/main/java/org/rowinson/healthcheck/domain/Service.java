package org.rowinson.healthcheck.domain;

import java.io.Serializable;
import java.util.Date;

public class Service implements Serializable {

  private String name;
  private String url;
  private Date createdAt;
  private Date updatedAt;

  public Service() { }

  public Service(String name, String url) {
    this.name = name;
    this.url = url;
    this.createdAt = new Date();
    this.updatedAt = this.createdAt;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return this.url;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }
}
