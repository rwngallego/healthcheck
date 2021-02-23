package org.rowinson.healthcheck;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rowinson.healthcheck.framework.Config;
import org.rowinson.healthcheck.framework.verticles.MainVerticle;

@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractVerticleTest {

  public static final String API_V1_USERS = "/api/v1/users/";
  public WebClient client;

  @BeforeAll
  void deploy_main_verticle(Vertx vertx, VertxTestContext testContext) {
    Config.SetJsonConfig(Config.CONF_CONFIG_TEST_JSON);
    this.client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(9999));

    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }
}
