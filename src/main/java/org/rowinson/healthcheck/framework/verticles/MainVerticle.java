package org.rowinson.healthcheck.framework.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main verticle in charge of deploying the other application verticles:
 * - ApiServerVerticle
 * - PollerWorkerVerticle
 * - PushServiceVerticle
 */
public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOG.info("Main Verticle started");

    vertx.deployVerticle(ApiServerVerticle.class.getName(),
      new DeploymentOptions().setInstances(Runtime.getRuntime().availableProcessors()))
      .onFailure(error -> {
        LOG.error("Failed to deploy {} verticle {}", ApiServerVerticle.class.getName(), error);
        startPromise.fail(error);
      })
      .onSuccess(id -> {
        LOG.info("{} deployed, id: {}", ApiServerVerticle.class.getName(), id);
        startPromise.complete();
      });
  }
}
