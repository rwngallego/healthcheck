package org.rowinson.healthcheck.adapters.handlers.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rowinson.healthcheck.AbstractVerticleTest;
import org.rowinson.healthcheck.domain.Service;

@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestCreateServiceHandler extends AbstractVerticleTest {

  Long userId;

  @BeforeEach
  void setup(Vertx vertx, VertxTestContext testContext) {
    client.post(API_V1_USERS)
      .sendJsonObject(new JsonObject().put("name", "test-user"))
      .onFailure(err -> testContext.failNow(err))
      .onComplete(testContext.succeeding(response -> {
        this.userId = response.bodyAsJsonObject().getLong("id");
        testContext.completeNow();
      }));
  }

  @Test
  void create_service(Vertx vertx, VertxTestContext testContext) {
    JsonObject params = new JsonObject();
    params.put("name", "test-service");
    params.put("url", "127.0.0.1");

    // we first create the test user, later get the service
    client.post("/api/v1/users/" + userId + "/services")
      .sendJsonObject(params)
      .onComplete(testContext.succeeding(response -> {
        var body = response.bodyAsJsonObject();
        var service = body.mapTo(Service.class);
        
        Assertions.assertEquals("test-service", service.getName());
        Assertions.assertEquals("127.0.0.1", service.getUrl());
        Assertions.assertEquals("UNKNOWN", service.getStatus());
        Assertions.assertNotEquals(0, body.getLong("id"));
        Assertions.assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }
}
