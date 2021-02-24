package org.rowinson.healthcheck.adapters.handlers.user;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rowinson.healthcheck.AbstractVerticleTest;
import org.rowinson.healthcheck.domain.User;

public class TestGetUsersHandler extends AbstractVerticleTest {

  @BeforeEach
  void setup(Vertx vertx, VertxTestContext testContext) {
    this.cleanEach()
      .compose(next -> createUser("user-1"))
      .compose(next -> createUser("user-2"))
      .onFailure(err -> testContext.failNow(err))
      .onComplete(testContext.succeeding(response -> {
        testContext.completeNow();
      }));
  }

  @Test
  void get_services(Vertx vertx, VertxTestContext testContext) {
    client.get(AbstractVerticleTest.API_V1_USERS)
      .send()
      .onComplete(testContext.succeeding(response -> {
        var body = response.bodyAsJsonArray();

        User user1 = body.getJsonObject(0).mapTo(User.class);
        User user2 = body.getJsonObject(1).mapTo(User.class);

        Assertions.assertEquals("user-1", user1.getName());
        Assertions.assertEquals("user-2", user2.getName());
        Assertions.assertNotEquals(0, user1.getId());
        Assertions.assertNotEquals(0, user2.getId());
        Assertions.assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }

  Future<Long> createUser(String name) {
    return client.post(API_V1_USERS)
      .sendJsonObject(new JsonObject().put("name", name))
      .compose(response -> {
        var body = response.bodyAsJsonObject();
        var user = body.mapTo(User.class);
        return Future.succeededFuture(user.getId());
      });
  }
}
