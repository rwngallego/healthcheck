package org.rowinson.healthcheck.adapters.handlers.user;

import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;
import org.rowinson.healthcheck.adapters.repositories.MySQLUserRepository;
import org.rowinson.healthcheck.application.UserApplication;
import org.rowinson.healthcheck.application.interfaces.UserRepository;
import org.rowinson.healthcheck.framework.verticles.ApiServerVerticle;

public class UserApi {
  public static void attachHandlers(Router router, MySQLPool pool) {
    UserRepository repo = new MySQLUserRepository(pool);
    UserApplication app = new UserApplication(repo);

    router.post(ApiServerVerticle.PREFIX + "/users").handler(new CreateUserHandler(app));
    router.get(ApiServerVerticle.PREFIX + "/users").handler(new GetUsersHandler(app));
  }
}
