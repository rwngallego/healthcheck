package org.rowinson.healthcheck;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rowinson.healthcheck.framework.Config;
import org.rowinson.healthcheck.framework.Database;
import org.rowinson.healthcheck.framework.verticles.MainVerticle;

/**
 * Configures vertx, deploy the main verticle and cleans the DB on every run
 */
@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractVerticleTest {

  public static final String API_V1_USERS = "/api/v1/users/";
  public WebClient client;
  public MySQLPool pool;

  @BeforeAll
  void deploy_main_verticle(Vertx vertx, VertxTestContext testContext) {
    this.client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(9999));

    Config.SetJsonConfig(Config.CONF_CONFIG_TEST_JSON);
    Config.GetValues(vertx)
      .onSuccess(config -> {
        pool = Database.GetPool(vertx, config);
      })
      .compose(next -> vertx.deployVerticle(new MainVerticle()))
      .onFailure(error -> testContext.failNow(error))
      .onSuccess(next -> testContext.completeNow());
  }

  protected Future<Boolean> cleanEach() {
    // We cleanup the DB after each test
    return Database.Cleanup(this.pool);
  }
}
