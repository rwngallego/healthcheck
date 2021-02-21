package org.rowinson.healthcheck;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Config {

  public static final String WEB_PORT = "WEB_PORT";
  public static final String DB_HOST = "DB_HOST";
  public static final String DB_PORT = "DB_PORT";
  public static final String DB_DATABASE = "DB_DATABASE";
  public static final String DB_USER = "DB_USER";
  public static final String DB_PASSWORD = "DB_PASSWORD";
  public static final String DB_POOL_SIZE = "DB_POOL_SIZE";

  /**
   * We get the configuration object from multiple stores
   * We first get the values from json file and override with the
   * env values.
   * @param vertx
   * @return
   */
  public static Future<JsonObject> GetValues(Vertx vertx) {
    // Stores
    ConfigStoreOptions fileStore = new ConfigStoreOptions()
      .setType("file")
      .setConfig(new JsonObject().put("path", "conf/config.json"));
    ConfigStoreOptions env = new ConfigStoreOptions()
      .setType("env");

    ConfigRetrieverOptions options = new ConfigRetrieverOptions()
      .addStore(fileStore).addStore(env);
    ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
    return retriever.getConfig();
  }

}
