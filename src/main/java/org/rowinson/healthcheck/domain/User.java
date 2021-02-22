package org.rowinson.healthcheck.domain;

import io.vertx.core.json.JsonObject;
import lombok.Data;

@Data
public class User {
  private long id;
  private String name;

  public JsonObject toJson(){
    return JsonObject.mapFrom(this);
  }
}
