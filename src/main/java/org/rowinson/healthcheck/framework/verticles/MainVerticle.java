package org.rowinson.healthcheck.framework.verticles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.rowinson.healthcheck.adapters.repositories.MySQLServiceRepository;
import org.rowinson.healthcheck.application.ServiceApplication;
import org.rowinson.healthcheck.domain.Service;
import org.rowinson.healthcheck.framework.Config;
import org.rowinson.healthcheck.framework.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * Main verticle in charge of deploying the other application verticles:
 * - Runs the pending migrations
 * - Deploys:
 * - ApiServerVerticle
 * - PollerVerticle
 */
public class MainVerticle extends AbstractVerticle {

  public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'";
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  private ServiceApplication serviceApplication;

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

    // Deploys all the children verticles
    Config.GetValues(vertx)
      .compose(this::setupDb)
      .compose(next -> handleServiceStatusUpdates())
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

  /**
   * Get the DB pool, creates the service application and executes
   * the migrations
   *
   * @param config
   * @return
   */
  private Future<Void> setupDb(JsonObject config) {
    var pool = Database.GetPool(vertx, config);
    var repo = new MySQLServiceRepository(pool);
    this.serviceApplication = new ServiceApplication(repo);
    return Database.Migrate(vertx, config);
  }

  /**
   * For time constraints this is a single DB pool in the MainVerticle,
   * but can be moved to a separate Verticle deployed multiple times
   * with multiple pools (but with caution because
   * could hammer the DB heavily)
   *
   * @return
   */
  private Future<Void> handleServiceStatusUpdates() {
    EventBus eb = vertx.eventBus();
    eb.<JsonObject>consumer(PollerVerticle.MSG_SERVICE_STATUS_SUCCEEDED)
      .handler(m -> {
        var service = m.body().mapTo(Service.class);
        // we need a basic cache mechanism here (memcached) to avoid changing
        // the status if the status hasn't changed
        serviceApplication.changeServiceStatus(service, Service.STATUS_OK);
      });
    eb.<JsonObject>consumer(PollerVerticle.MSG_SERVICE_STATUS_FAILED)
      .handler(m -> {
        var service = m.body().mapTo(Service.class);
        // we need a basic cache mechanism here (memcached) to avoid changing
        // the status if the status hasn't changed
        serviceApplication.changeServiceStatus(service, Service.STATUS_FAIL);
      });
    return Future.succeededFuture();
  }
}
