package org.rowinson.healthcheck.adapters.handlers.user;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.rowinson.healthcheck.application.UserApplication;
import org.rowinson.healthcheck.framework.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http handler for getting the list of users
 *
 * @implNote It needs to be paginated
 */
public class GetUsersHandler implements Handler<RoutingContext> {
  public static final Logger LOG = LoggerFactory.getLogger(GetUsersHandler.class);

  private UserApplication userApplication;

  public GetUsersHandler(UserApplication userApplication) {
    this.userApplication = userApplication;
  }

  @Override
  public void handle(RoutingContext context) {
    userApplication.getAllUsers(0, 10, "name", "asc")
      .onFailure(Http.handleFailure(context, "Could not get the users"))
      .onSuccess(users -> {
        JsonArray result = new JsonArray();
        users.stream().forEach(a -> result.add(a));

        LOG.info("Get users response: {}", result.encode());

        context.response()
          .putHeader(Http.CONTENT_TYPE, Http.APPLICATION_JSON)
          .end(result.toBuffer());
      });
  }
}
