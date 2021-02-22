package org.rowinson.healthcheck.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Service {
  private long id;
  @JsonProperty("user_id")
  private long userId;
  private String name;
  private String url;
  private String status;
  @JsonProperty("created_at")
  private LocalDateTime createdAt;
  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;

  public JsonObject toJson(){
    return JsonObject.mapFrom(this);
  }
}
