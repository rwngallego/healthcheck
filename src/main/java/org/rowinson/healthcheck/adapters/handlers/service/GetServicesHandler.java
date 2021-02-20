package org.rowinson.healthcheck.adapters.handlers.service;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.rowinson.healthcheck.domain.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetServicesHandler implements Handler<RoutingContext> {
  public static final Logger LOG = LoggerFactory.getLogger(GetServicesHandler.class);

  @Override
  public void handle(RoutingContext context) {
    Service account = new Service("Account", "127.0.0.1:2001");
    JsonArray services = new JsonArray();
    services.add(account);

    LOG.info("{} | {}", context.normalizedPath(), services.encode());
    context.response()
      .putHeader("Content-Type", "application/json")
      .end(services.toBuffer());
  }
}
