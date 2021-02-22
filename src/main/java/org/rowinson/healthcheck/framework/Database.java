package org.rowinson.healthcheck.framework;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import org.flywaydb.core.Flyway;

/**
 * This class is in charge of the DB interactions
 */
public class Database {

  /**
   * Executes the DB migrations
   * @param vertx
   * @param config
   * @return
   */
  public static Future<Void> Migrate(Vertx vertx, JsonObject config) {
    return vertx.executeBlocking(promise -> {
      //TODO Currently uses ssl = false
      final String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false",
        config.getString(Config.DB_HOST),
        config.getInteger(Config.DB_PORT),
        config.getString(Config.DB_DATABASE)
      );

      Flyway flyway = Flyway.configure()
        .dataSource(url, config.getString(Config.DB_USER), config.getString(Config.DB_PASSWORD))
        .load();

      flyway.migrate();
      promise.complete();
    });
  }

  /**
   * Gets the DB pool using the provided config options
   * @param vertx
   * @param config
   * @return
   */
  public static MySQLPool GetPool(Vertx vertx, JsonObject config) {
    MySQLConnectOptions connectionOptions = new MySQLConnectOptions()
      .setPort(config.getInteger(Config.DB_PORT))
      .setHost(config.getString(Config.DB_HOST))
      .setDatabase(config.getString(Config.DB_DATABASE))
      .setUser(config.getString(Config.DB_USER))
      .setPassword(config.getString(Config.DB_PASSWORD));

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(config.getInteger(Config.DB_POOL_SIZE));

    return MySQLPool.pool(vertx, connectionOptions, poolOptions);
  }

  /**
   * Executes the DB clean up
   * @param pool
   * @return
   */
  public static Future<Boolean> Cleanup(MySQLPool pool) {
    return pool.withConnection(connection -> connection
        .query("DELETE FROM services")
        .execute()
        .compose(ra ->
          connection.query("DELETE FROM users")
            .execute()
          .compose(rb -> Future.succeededFuture(true))
        )
    );
  }
}

