package org.rowinson.healthcheck.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rowinson.healthcheck.AbstractHealthCheckTest;

import java.time.LocalDateTime;

public class TestService extends AbstractHealthCheckTest {
  @Test
  void testServiceToJson () {
    LocalDateTime newCreatedAt = LocalDateTime.now();
    LocalDateTime newUpdatedAt = LocalDateTime.now();

    Service service = new Service();
    service.setId(7);
    service.setUserId(12);
    service.setName("new-test-service");
    service.setUrl("192.168.0.1");
    service.setCreatedAt(newCreatedAt);
    service.setUpdatedAt(newUpdatedAt);

    var json = service.toJson();
    Assertions.assertEquals(7, json.getLong("id"));
    Assertions.assertEquals(12, json.getLong("user_id"));
    Assertions.assertEquals("new-test-service", json.getString("name"));
    Assertions.assertEquals("192.168.0.1", json.getString("url"));
    Assertions.assertNotNull(json.getString("created_at"));
    Assertions.assertNotNull(json.getString("updated_at"));
  }
}
