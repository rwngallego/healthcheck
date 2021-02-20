package org.rowinson.healthcheck.domain.entities;

import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.util.Date;

@Data
public class ServiceEntity {
  private String name;
  private String url;
  private Date createdAt;
  private Date updatedAt;

  public JsonObject toJson(){
    return JsonObject.mapFrom(this);
  }
}
