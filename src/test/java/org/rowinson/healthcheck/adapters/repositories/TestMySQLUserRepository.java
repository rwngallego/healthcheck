package org.rowinson.healthcheck.adapters.repositories;

import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rowinson.healthcheck.AbstractHealthCheckTest;
import org.rowinson.healthcheck.domain.User;

public class TestMySQLUserRepository extends AbstractHealthCheckTest {

  @Test
  void testCreateUser(VertxTestContext testContext) {
    User user = new User();
    user.setName("test-user");
    userRepo.CreateUser(user)
      .onSuccess(userId -> {
        Assertions.assertNotNull(userId);
        testContext.completeNow();
      })
      .onFailure(e -> testContext.failNow(e));
  }

  @Test
  void testGetUsers(VertxTestContext testContext) {
    User user = new User();
    user.setName("test-get-users");
    userRepo.CreateUser(user)
      .compose(userId -> {
        user.setId(userId);
        return userRepo.GetAllUsers();
      })
      .onSuccess(results -> {
        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("test-get-users", results.get(0).getName());
        Assertions.assertEquals(user.getId(), results.get(0).getId());
        testContext.completeNow();
      })
      .onFailure(e -> testContext.failNow(e));
  }
}
