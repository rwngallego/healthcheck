package org.rowinson.healthcheck.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rowinson.healthcheck.AbstractHealthCheckTest;

public class TestUser extends AbstractHealthCheckTest {
  @Test
  void testUserToJson () {
    User user = new User();
    user.setId(7);
    user.setUsername("test-username");
    user.setPassword("test-password");

    var json = user.toJson();
    Assertions.assertEquals(7, json.getLong("id"));
    Assertions.assertEquals("test-username", json.getString("username"));
    Assertions.assertEquals("test-password", json.getString("password"));
  }
}
