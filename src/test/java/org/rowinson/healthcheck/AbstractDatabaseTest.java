package org.rowinson.healthcheck;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rowinson.healthcheck.adapters.repositories.MySQLServiceRepository;
import org.rowinson.healthcheck.adapters.repositories.MySQLUserRepository;
import org.rowinson.healthcheck.framework.Config;
import org.rowinson.healthcheck.framework.Database;
import org.rowinson.healthcheck.framework.verticles.MainVerticle;

import java.text.SimpleDateFormat;

/**
 * Configures vertx, setup the database and cleans it on every run
 */
@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractDatabaseTest {
  public MySQLServiceRepository serviceRepo;
  public MySQLUserRepository userRepo;
  public MySQLPool pool;

  @BeforeAll
  void setup_vertx(Vertx vertx, VertxTestContext testContext) {
    // Configure mapper
    SimpleDateFormat df = new SimpleDateFormat(MainVerticle.DATE_FORMAT);
    ObjectMapper mapper = io.vertx.core.json.jackson.DatabindCodec.mapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.setDateFormat(df);

    Config.SetJsonConfig(Config.CONF_CONFIG_TEST_JSON);
    Config.GetValues(vertx)
      .compose(config -> {
        MySQLPool pool = Database.GetPool(vertx, config);

        this.pool = pool;
        this.userRepo = new MySQLUserRepository(pool);
        this.serviceRepo = new MySQLServiceRepository(pool);
        return Database.Migrate(vertx, config);
      })
      .onSuccess(r -> {
        testContext.completeNow();
      })
      .onFailure(r -> {
        testContext.failNow(r);
      });
  }

  @AfterEach
  void clean_each(VertxTestContext testContext) {
    // We cleanup the DB after each test
    Database.Cleanup(this.pool)
      .onSuccess(r -> testContext.completeNow())
      .onFailure(r -> testContext.failNow(r));
  }
}
