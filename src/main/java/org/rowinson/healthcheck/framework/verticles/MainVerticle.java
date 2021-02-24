package org.rowinson.healthcheck.framework.verticles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import org.rowinson.healthcheck.framework.Config;
import org.rowinson.healthcheck.framework.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * Main verticle in charge of deploying the other application verticles:
 * - Runs the pending migrations
 * - Deploys:
 *    - ApiServerVerticle
 *    - PollerWorkerVerticle
 *    - PushServiceVerticle
 */
public class MainVerticle extends AbstractVerticle {

  public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'";
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOG.info("Main Verticle starting");

    // Register the generic exception handler
    vertx.exceptionHandler(error -> {
      LOG.error("Unhandled exception: {}", error);
    });

    // Add the Jackson Date formatter
    SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
    ObjectMapper mapper = io.vertx.core.json.jackson.DatabindCodec.mapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.setDateFormat(df);

    Config.GetValues(vertx)
      .compose(config -> Database.Migrate(vertx, config))
      .compose(next -> vertx.deployVerticle(ApiServerVerticle.class.getName(), new DeploymentOptions().setInstances(Runtime.getRuntime().availableProcessors())))
      .compose(next -> vertx.deployVerticle(PollerVerticle.class.getName(), new DeploymentOptions().setWorker(true)))
      .onFailure(error -> {
        LOG.error("Failed to deploy {} verticles", error);
        startPromise.fail(error);
      })
      .onSuccess(id -> {
        LOG.info("{} deployed, id: {}", ApiServerVerticle.class.getName(), id);
        LOG.info("{} deployed, id: {}", PollerVerticle.class.getName(), id);
        startPromise.complete();
      });
  }
}
