package org.rowinson.healthcheck.adapters.handlers.user;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.rowinson.healthcheck.adapters.handlers.service.GetServicesHandler;
import org.rowinson.healthcheck.application.UserApplication;
import org.rowinson.healthcheck.domain.User;
import org.rowinson.healthcheck.framework.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserHandler implements Handler<RoutingContext> {

  public static final Logger LOG = LoggerFactory.getLogger(GetServicesHandler.class);

  private UserApplication userApplication;

  public CreateUserHandler(UserApplication userApplication) {
    this.userApplication = userApplication;
  }

  @Override
  public void handle(RoutingContext context) {
    var params = context.getBodyAsJson();
    var user = params.mapTo(User.class);

    LOG.info("Creating user {}", user.getName());

    userApplication.createUser(user)
      .onFailure(Http.handleFailure(context, "Could not create the user"))
      .compose(userId -> userApplication.getUserById(userId))
      .onFailure(Http.handleFailure(context, "Could not retrieve the created user"))
      .onSuccess(created -> {
        LOG.info("User created: {}", created);
        context.response()
          .putHeader(Http.CONTENT_TYPE, Http.APPLICATION_JSON)
          .setStatusCode(200)
          .end(created.toJson().toBuffer());
      });
  }
}
