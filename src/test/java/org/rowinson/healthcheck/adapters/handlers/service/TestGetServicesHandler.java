package org.rowinson.healthcheck.adapters.handlers.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rowinson.healthcheck.AbstractVerticleTest;
import org.rowinson.healthcheck.domain.Service;

public class TestGetServicesHandler extends AbstractVerticleTest {

  Long userId;

  @BeforeEach
  void setup(Vertx vertx, VertxTestContext testContext) {
    this.cleanEach()
      .compose(next -> client.post(API_V1_USERS).sendJsonObject(new JsonObject().put("name", "test-user")))
      .onFailure(err -> testContext.failNow(err))
      .onSuccess(response -> {
        this.userId = response.bodyAsJsonObject().getLong("id");
      })
      .compose(next -> createService(testContext, userId, "test-service-1", "127.0.0.1"))
      .compose(next -> createService(testContext, userId, "test-service-2", "127.0.0.2"))
      .onComplete(next -> testContext.completeNow());
  }

  @Test
  void get_services(Vertx vertx, VertxTestContext testContext) {
    // we first create the test user, later get the service
    client.get("/api/v1/users/" + userId + "/services")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var body = response.bodyAsJsonArray();

        Service service1 = body.getJsonObject(0).mapTo(Service.class);
        Service service2 = body.getJsonObject(1).mapTo(Service.class);

        Assertions.assertEquals("test-service-1", service1.getName());
        Assertions.assertEquals("test-service-2", service2.getName());
        Assertions.assertEquals("127.0.0.1", service1.getUrl());
        Assertions.assertEquals("127.0.0.2", service2.getUrl());
        Assertions.assertEquals("UNKNOWN", service1.getStatus());
        Assertions.assertEquals("UNKNOWN", service2.getStatus());
        Assertions.assertNotEquals(0, service1.getId());
        Assertions.assertNotEquals(0, service2.getId());
        Assertions.assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }

  Future<Long> createService(VertxTestContext testContext, long userId, String name, String url) {
    JsonObject params = new JsonObject();
    params.put("name", name);
    params.put("url", url);
    return client.post(API_V1_USERS + userId + "/services")
      .sendJsonObject(params)
      .onFailure(error -> testContext.failNow(error))
      .compose(response -> {
        var body = response.bodyAsJsonObject();
        var service = body.mapTo(Service.class);
        return Future.succeededFuture(service.getId());
      });
  }
}
