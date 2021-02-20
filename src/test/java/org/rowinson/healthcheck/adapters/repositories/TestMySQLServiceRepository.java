package org.rowinson.healthcheck.adapters.repositories;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rowinson.healthcheck.Db;

@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestMySQLServiceRepository {
  JsonObject config;
  MySQLPool pool;

  @BeforeAll
  void migrateDB(Vertx vertx, VertxTestContext testContext) {
    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    System.out.println("AAAAA");
    retriever.getConfig()
      .compose(config -> {
        System.out.println("AAAAA");
        this.config = config;
        this.pool = getPool(vertx, config);
        return Db.Migrate(vertx, config);
      })
      .onSuccess(r -> {
        System.out.println("AAAAA");
        testContext.completeNow();
      })
    .onFailure(r -> {
      testContext.failNow(r);
    });
  }

  @Test
  void testGetAllServices(Vertx vertx, VertxTestContext testContext) {
    MySQLServiceRepository repo = new MySQLServiceRepository(pool);
    repo.GetAllServices(0, 10, "", "")
    .onSuccess(r -> {
      r.forEach(row -> {
        System.out.println(row.getString("name"));
        testContext.completeNow();
      });
    }).onFailure(r -> {
      testContext.failNow(r);
    });
  }

  MySQLPool getPool(Vertx vertx, JsonObject config) {
    MySQLConnectOptions connectionOptions = new MySQLConnectOptions()
      .setPort(config.getInteger("db-port"))
      .setHost(config.getString("db-host"))
      .setDatabase(config.getString("db-database"))
      .setUser(config.getString("db-user"))
      .setPassword(config.getString("db-password"));

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(config.getInteger("db-pool-size"));

    return MySQLPool.pool(vertx, connectionOptions, poolOptions);
  }
}
