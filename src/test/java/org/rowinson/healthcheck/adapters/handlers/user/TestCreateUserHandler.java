package org.rowinson.healthcheck.adapters.handlers.user;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rowinson.healthcheck.AbstractVerticleTest;

@ExtendWith(VertxExtension.class)
public class TestCreateUserHandler extends AbstractVerticleTest {


  @Test
  void create_user(Vertx vertx, VertxTestContext testContext) {
    JsonObject params = new JsonObject();
    params.put("name", "test-user");

    client.post(API_V1_USERS)
      .sendJsonObject(params)
      .onComplete(testContext.succeeding(response -> {
        var body = response.bodyAsJsonObject();
        Assertions.assertEquals("test-user", body.getString("name"));
        Assertions.assertNotEquals(0, body.getLong("id"));
        Assertions.assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }
}
