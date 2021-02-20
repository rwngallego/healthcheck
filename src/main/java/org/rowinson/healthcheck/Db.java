package org.rowinson.healthcheck;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.flywaydb.core.Flyway;

public class Db {

  public static Future<Void> Migrate(Vertx vertx, JsonObject config) {
    return vertx.executeBlocking(promise -> {
      //TODO Currently uses ssl = false
      final String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false",
        config.getString("db-host"),
        config.getInteger("db-port"),
        config.getString("db-database")
      );

      Flyway flyway = Flyway.configure()
        .dataSource(url, config.getString("db-user"), config.getString("db-password"))
        .load();

      flyway.migrate();
      promise.complete();
    });
  }
}
