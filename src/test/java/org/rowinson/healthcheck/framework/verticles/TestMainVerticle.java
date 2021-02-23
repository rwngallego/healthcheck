package org.rowinson.healthcheck.framework.verticles;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rowinson.healthcheck.AbstractVerticleTest;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle extends AbstractVerticleTest {
  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }
}
