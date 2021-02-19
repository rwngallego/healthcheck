package org.rowinson.healthcheck.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;

public class TestServiceUser {
  @Test
  void testNewEmpty () {
    Service service = new Service();

    assertNull(service.getName());
    assertNull(service.getUrl());
    assertNull(service.getCreatedAt());
    assertNull(service.getUpdatedAt());
  }

  @Test
  void testNew () {
    Date createdAt = new Date();
    Date updatedAt = new Date();
    Service service = new Service("test-service", "127.0.0.1", createdAt, updatedAt);

    assertEquals("test-service", service.getName());
    assertEquals("127.0.0.1", service.getUrl());
    assertEquals(createdAt, service.getCreatedAt());
    assertEquals(updatedAt, service.getUpdatedAt());
  }

  @Test
  void testGetSet () {
    Date newCreatedAt = new Date();
    Date newUpdatedAt = new Date();
    Service service = new Service("test-service", "127.0.0.1", new Date(), new Date());
    service.setName("new-test-service");
    service.setUrl("192.168.0.1");
    service.setCreatedAt(newCreatedAt);
    service.setUpdatedAt(newUpdatedAt);

    assertEquals("new-test-service", service.getName());
    assertEquals("192.168.0.1", service.getUrl());
    assertEquals(newCreatedAt, service.getCreatedAt());
    assertEquals(newUpdatedAt, service.getUpdatedAt());
  }
}
