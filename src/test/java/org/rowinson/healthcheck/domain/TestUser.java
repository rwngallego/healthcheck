package org.rowinson.healthcheck.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rowinson.healthcheck.AbstractHealthCheckTest;

public class TestUser extends AbstractHealthCheckTest {
  @Test
  void testUserToJson () {
    User user = new User();
    user.setId(7);
    user.setName("test-name");

    var json = user.toJson();
    Assertions.assertEquals(7, json.getLong("id"));
    Assertions.assertEquals("test-name", json.getString("name"));
  }
}
