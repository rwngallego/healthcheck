package org.rowinson.healthcheck.framework;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Handle the application configuration. It can be
 * extended to detect configuration changes.
 */
public class Config {

  public static final String WEB_PORT = "WEB_PORT";
  public static final String DB_HOST = "DB_HOST";
  public static final String DB_PORT = "DB_PORT";
  public static final String DB_DATABASE = "DB_DATABASE";
  public static final String DB_USER = "DB_USER";
  public static final String DB_PASSWORD = "DB_PASSWORD";
  public static final String DB_POOL_SIZE = "DB_POOL_SIZE";
  public static final String CONF_CONFIG_JSON = "conf/config.json";
  public static final String CONF_CONFIG_TEST_JSON = "conf/config.test.json";

  /**
   * Get the configuration object from multiple stores.
   * First get the values from the default json file and then override them with the
   * ENV values.
   * @param vertx
   * @return
   */
  public static Future<JsonObject> GetValues(Vertx vertx) {
    return GetValues(vertx, CONF_CONFIG_JSON);
  }

  /**
   * Get the configuration object from multiple stores, primarily from the
   * specified confConfigJson file. First get the values from the json file
   * and then override them with the ENV values.
   * @param vertx
   * @param confConfigJson
   * @return
   */
  public static Future<JsonObject> GetValues(Vertx vertx, String confConfigJson) {
    // Stores
    ConfigStoreOptions fileStore = new ConfigStoreOptions()
      .setType("file")
      .setConfig(new JsonObject().put("path", confConfigJson));
    ConfigStoreOptions env = new ConfigStoreOptions()
      .setType("env");

    ConfigRetrieverOptions options = new ConfigRetrieverOptions()
      .addStore(fileStore).addStore(env);
    ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
    return retriever.getConfig();
  }

}
