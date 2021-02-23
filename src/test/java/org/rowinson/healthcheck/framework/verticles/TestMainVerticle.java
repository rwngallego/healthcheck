package org.rowinson.healthcheck.framework.verticles;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rowinson.healthcheck.framework.Config;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {
  @BeforeEach
  void deploy_main_verticle(Vertx vertx, VertxTestContext testContext) {
    Config.SetJsonConfig(Config.CONF_CONFIG_TEST_JSON);
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }
}
