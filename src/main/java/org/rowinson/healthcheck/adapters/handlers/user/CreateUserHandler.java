package org.rowinson.healthcheck.adapters.handlers.user;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.rowinson.healthcheck.application.UserApplication;

public class CreateUserHandler implements Handler<RoutingContext> {

  private UserApplication userApplication;

  public CreateUserHandler(UserApplication userApplication) {
   userApplication = userApplication;
  }

  @Override
  public void handle(RoutingContext context) {

  }
}
