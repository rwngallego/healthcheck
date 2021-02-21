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

import java.text.SimpleDateFormat;

@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractHealthCheckTest {
  public MySQLServiceRepository serviceRepo;
  public MySQLUserRepository userRepo;
  public MySQLPool pool;

  @BeforeAll
  void setupVertx(Vertx vertx, VertxTestContext testContext) {
    // Configure mapper
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    ObjectMapper mapper = io.vertx.core.json.jackson.DatabindCodec.mapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.setDateFormat(df);

     Config.GetValues(vertx)
      .compose(config -> {
        // Setup a different DB for testing
        String dbTest = config.getString(Config.DB_DATABASE) + "_test";
        config.put(Config.DB_DATABASE, dbTest);

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
  void cleanEach(VertxTestContext testContext) {
    // We cleanup the DB after each test
    Database.Cleanup(this.pool)
      .onSuccess(r -> testContext.completeNow())
      .onFailure(r -> testContext.failNow(r));
  }
}
