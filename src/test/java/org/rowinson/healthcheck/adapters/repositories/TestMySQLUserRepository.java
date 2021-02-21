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
    user.setUsername("test-user");
    user.setPassword("pass");
    userRepo.CreateUser(user)
      .onSuccess(userId -> {
        Assertions.assertNotNull(userId);
        testContext.completeNow();
      })
      .onFailure(e -> testContext.failNow(e));
  }

  @Test
  void testGetByUsernamePassword(VertxTestContext testContext) {
    User user = new User();
    user.setUsername("test-user-with-password");
    user.setPassword("pass-2");
    userRepo.CreateUser(user)
      .compose(next -> userRepo.GetByUsernameAndPassword("test-user-with-password", "pass-2"))
      .onSuccess(created -> {
        Assertions.assertNotNull(created);
        Assertions.assertEquals("test-user-with-password", created.getUsername());
        Assertions.assertEquals("pass-2", created.getPassword());
        testContext.completeNow();
      })
      .onFailure(e -> testContext.failNow(e));
  }
}
