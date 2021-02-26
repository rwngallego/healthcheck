package org.rowinson.healthcheck.domain;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
  private long id;
  private String name;

  /**
   * Gets the JsonObject representation
   * @return
   */
  public JsonObject toJson(){
    return JsonObject.mapFrom(this);
  }
}
