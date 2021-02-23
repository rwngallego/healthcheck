package org.rowinson.healthcheck.framework.verticles;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rowinson.healthcheck.domain.Service;
import org.rowinson.healthcheck.framework.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestPollerVerticle {

  public static final Service SERVICE_1 = new Service(1, 1001, "service-1", "127.0.0.1:7001", "UNKNOWN", LocalDateTime.now(), LocalDateTime.now());
  public static final Service SERVICE_2 = new Service(2, 1002, "service-2", "127.0.0.1:7002", "UNKNOWN", LocalDateTime.now(), LocalDateTime.now());
  public static final Service SERVICE_3 = new Service(3, 1003, "service-3", "127.0.0.1:7003", "UNKNOWN", LocalDateTime.now(), LocalDateTime.now());
  public static final int PORT_1 = 7001;
  public static final int PORT_2 = 7002;
  public static final int PORT_3 = 7003;
  public static final int SLEEP_1 = 500;
  public static final int SLEEP_2 = 2000;
  public static final int SLEEP_3 = 1000;
  private static final Logger LOG = LoggerFactory.getLogger(TestPollerVerticle.class);

  @BeforeAll
  void setup(Vertx vertx, VertxTestContext testContext) {
    Config.SetJsonConfig(Config.CONF_CONFIG_TEST_JSON);
    EventBus eb = vertx.eventBus();

    vertx.deployVerticle(new PollerVerticle())
      .compose(next -> startFakeEndpoint(vertx, PORT_1, SLEEP_1))
      .compose(next -> startFakeEndpoint(vertx, PORT_2, SLEEP_2))
      .compose(next -> startFakeEndpoint(vertx, PORT_3, SLEEP_3))
      .onFailure(error -> testContext.failNow(error))
      .onSuccess(next -> {
        // Register services in the worker
        eb.publish("service.created", JsonObject.mapFrom(SERVICE_1));
        eb.publish("service.created", JsonObject.mapFrom(SERVICE_2));
        eb.publish("service.created", JsonObject.mapFrom(SERVICE_3));

        testContext.completeNow();
      });
  }

  @Test
  void poll_clients(Vertx vertx, VertxTestContext testContext) {
    ArrayList<Long> processedIds = new ArrayList<>();
    ArrayList<Long> expectedSucceededIds = new ArrayList<>();
    expectedSucceededIds.add(SERVICE_1.getId());
    expectedSucceededIds.add(SERVICE_3.getId());

    EventBus eb = vertx.eventBus();

    testContext.succeeding(next -> {

      LOG.debug("Subscribing to messages");
      eb.<JsonObject>consumer("service.status.failed")
        .handler(m -> {
          var service = m.body();
          var serviceId = service.getLong("id");

          processedIds.add(serviceId);
          LOG.debug("service.status.failed received: {}, processed: {}", service.toString(), processedIds.toString());

          Assertions.assertEquals(SERVICE_2.getId(), serviceId);
          checkCompletedTest(testContext, 3, processedIds);
        });
      eb.<JsonObject>consumer("service.status.succeeded")
        .handler(m -> {
          var service = m.body();
          var serviceId = service.getLong("id");

          processedIds.add(serviceId);
          LOG.debug("service.status.succeeded received: {}, processed: {}", service, processedIds.toString());

          Assertions.assertTrue(expectedSucceededIds.contains(serviceId));
          checkCompletedTest(testContext, 3, processedIds);
        });
    });
  }

  void checkCompletedTest(VertxTestContext testContext, int expected, ArrayList<Long> completed) {
    if (completed.size() == expected) {
      testContext.completeNow();
    }
  }

  Future<Void> startFakeEndpoint(Vertx vertx, int port, long sleepMs) {
    return vertx.createHttpServer()
      .requestHandler(req -> {
          LOG.debug("Starting request on {}", port);
          vertx.executeBlocking(c -> {
            try {
              Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            req.response().end("OK");

            LOG.debug("Request done on {}", port);
            c.complete();
          });
        }
      )
      .listen(port)
      .compose(next -> {
        LOG.debug("Deployed fake client on {}", port);
        return Future.succeededFuture();
      });
  }
}
