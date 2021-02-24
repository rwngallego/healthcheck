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
import org.rowinson.healthcheck.AbstractDatabaseTest;
import org.rowinson.healthcheck.domain.Service;
import org.rowinson.healthcheck.framework.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestPollerVerticle extends AbstractDatabaseTest {

  public static final Service SERVICE_1 = new Service(1, 1001, "service-1", "http://127.0.0.1:7001", "UNKNOWN", LocalDateTime.now(), LocalDateTime.now());
  public static final Service SERVICE_2 = new Service(2, 1002, "service-2", "http://127.0.0.1:7002", "UNKNOWN", LocalDateTime.now(), LocalDateTime.now());
  public static final Service SERVICE_3 = new Service(3, 1003, "service-3", "http://127.0.0.1:7003", "UNKNOWN", LocalDateTime.now(), LocalDateTime.now());
  public static final int PORT_1 = 7001;
  public static final int PORT_2 = 7002;
  public static final int PORT_3 = 7003;
  public static final int SLEEP_1 = 10;
  public static final int SLEEP_2 = 700;
  private static final Logger LOG = LoggerFactory.getLogger(TestPollerVerticle.class);
  public static final int STATUS_1 = 200;
  public static final int STATUS_2 = 200;
  public static final int STATUS_3 = 500;

  @BeforeAll
  void setup(Vertx vertx, VertxTestContext testContext) {
    Config.SetJsonConfig(Config.CONF_CONFIG_TEST_JSON);
    EventBus eb = vertx.eventBus();

    vertx.deployVerticle(new PollerVerticle())
      .compose(next -> startFakeEndpoint(vertx, PORT_1, SLEEP_1, STATUS_1))
      .compose(next -> startFakeEndpoint(vertx, PORT_2, SLEEP_2, STATUS_2))
      .compose(next -> startFakeEndpoint(vertx, PORT_3, 0L, STATUS_3))
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
    ArrayList<Long> expectedFailedIds = new ArrayList<>();
    expectedFailedIds.add(SERVICE_2.getId());
    expectedFailedIds.add(SERVICE_3.getId());

    EventBus eb = vertx.eventBus();

    LOG.debug("Subscribing to messages");
    eb.<JsonObject>consumer(PollerVerticle.MSG_SERVICE_STATUS_FAILED)
      .handler(m -> {
        var service = m.body().mapTo(Service.class);
        var serviceId = service.getId();

        processedIds.add(serviceId);
        LOG.debug("{} received: {}, processed: {}", PollerVerticle.MSG_SERVICE_STATUS_FAILED, service.toString(), processedIds.toString());

        Assertions.assertTrue(expectedFailedIds.contains(serviceId));
        checkCompletedTest(testContext, 3, processedIds);
      });
    eb.<JsonObject>consumer(PollerVerticle.MSG_SERVICE_STATUS_SUCCEEDED)
      .handler(m -> {
        var service = m.body().mapTo(Service.class);
        var serviceId = service.getId();

        processedIds.add(serviceId);
        LOG.debug("{} received: {}, processed: {}", PollerVerticle.MSG_SERVICE_STATUS_SUCCEEDED, service.toString(), processedIds.toString());

        Assertions.assertEquals(SERVICE_1.getId(), serviceId);
        checkCompletedTest(testContext, 3, processedIds);
      });
  }

  void checkCompletedTest(VertxTestContext testContext, int expected, ArrayList<Long> completed) {
    if (completed.size() == expected) {
      // wait some time for the test endpoints to complete the remaining requests
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
      }
      testContext.completeNow();
    }
  }

  Future<Void> startFakeEndpoint(Vertx vertx, int port, long sleepMs, int status) {
    return vertx.createHttpServer()
      .requestHandler(req -> {
          LOG.debug("Client request start on {}", port);
          vertx.executeBlocking(c -> {
            try {
              Thread.sleep(sleepMs);
            } catch (Exception e) {
            }
            req.response().setStatusCode(status).end("");

            LOG.debug("Client request done on {}", port);
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
