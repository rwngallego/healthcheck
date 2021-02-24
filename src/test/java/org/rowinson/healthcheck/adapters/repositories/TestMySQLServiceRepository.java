package org.rowinson.healthcheck.adapters.repositories;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rowinson.healthcheck.AbstractDatabaseTest;
import org.rowinson.healthcheck.domain.Service;
import org.rowinson.healthcheck.domain.User;

@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestMySQLServiceRepository extends AbstractDatabaseTest {
  Long userId;

  @BeforeEach
  void setup_each(VertxTestContext testContext) {
    createUser()
      .onSuccess(userId -> {
        this.userId = userId;
        testContext.completeNow();
      })
      .onFailure(r -> testContext.failNow(r));
  }

  @Test
  void test_get_all_services(VertxTestContext testContext) {
    createTwoServices()
      .compose(next -> serviceRepo.getPaginatedServices(this.userId, 0, 10, "", ""))
      .onSuccess(results -> {
        Assertions.assertEquals(2, results.size());
        testContext.completeNow();
      }).onFailure(r -> {
      testContext.failNow(r);
    });
  }

  @Test
  void test_get_service(VertxTestContext testContext) {
    Service service = new Service();
    service.setUserId(this.userId);
    service.setName("test-service-1");
    service.setUrl("test-url-1");

    serviceRepo.createService(service)
      .compose(serviceId -> serviceRepo.getService(this.userId, serviceId))
      .onSuccess(result -> {
        Assertions.assertEquals("test-service-1", result.getName());
        Assertions.assertEquals("test-url-1", result.getUrl());
        Assertions.assertEquals(this.userId, result.getUserId());
        testContext.completeNow();
      }).onFailure(r -> {
      testContext.failNow(r);
    });
  }

  @Test
  void test_create_service(VertxTestContext testContext) {
    Service service = new Service();
    service.setUserId(userId);
    service.setName("test-service");
    service.setUrl("http://127.0.0.1/");
    serviceRepo.createService(service)
      .compose(serviceId -> serviceRepo.getService(userId, serviceId))
      .onSuccess(created -> {
        Assertions.assertEquals("test-service", created.getName());
        Assertions.assertEquals("http://127.0.0.1/", created.getUrl());
        testContext.completeNow();
      })
      .onFailure(e -> testContext.failNow(e));
  }

  @Test
  void test_update_service(VertxTestContext testContext) {
    Service service = new Service();
    service.setUserId(userId);
    service.setName("test-service-update");
    service.setUrl("http://127.0.0.1/");
    serviceRepo.createService(service)
      .compose(serviceId -> {
        service.setId(serviceId);
        service.setName("test-service-update-after");
        service.setUrl("http://192.168.0.1/");
        return serviceRepo.updateService(service);
      })
      .compose(next -> serviceRepo.getService(userId, service.getId()))
      .onSuccess(updated -> {
        Assertions.assertEquals("test-service-update-after", updated.getName());
        Assertions.assertEquals("http://192.168.0.1/", updated.getUrl());
        testContext.completeNow();
      })
      .onFailure(e -> testContext.failNow(e));
  }

  @Test
  void test_delete_service(VertxTestContext testContext) {
    long serviceId;
    Service service = new Service();
    service.setUserId(userId);
    service.setName("test-service-update");
    service.setUrl("http://127.0.0.1/");
    serviceRepo.createService(service)
      .compose(id -> {
        service.setId(id);
        return serviceRepo.deleteService(id);
      })
      .compose(next -> serviceRepo.getService(userId, service.getId()))
      .onSuccess(retrieved -> {
        Assertions.assertNull(retrieved);
        testContext.completeNow();
      })
      .onFailure(e -> testContext.failNow(e));
  }

  private Future<Long> createUser() {
    User user = new User();
    user.setName("test-user");
    return userRepo.createUser(user);
  }

  private CompositeFuture createTwoServices() {
    Service service1 = new Service();
    service1.setUserId(this.userId);
    service1.setName("test-service-1");
    service1.setUrl("test-url-1");

    Service service2 = new Service();
    service2.setUserId(this.userId);
    service2.setName("test-service-2");
    service2.setUrl("test-url-2");
    return CompositeFuture.all(serviceRepo.createService(service1), serviceRepo.createService(service2));
  }
}
