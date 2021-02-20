package org.rowinson.healthcheck.adapters.handlers;

import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;
import org.rowinson.healthcheck.adapters.handlers.user.CreateUserHandler;
import org.rowinson.healthcheck.adapters.repositories.MySQLUserRepository;
import org.rowinson.healthcheck.adapters.verticles.ApiServerVerticle;
import org.rowinson.healthcheck.application.UserApplication;
import org.rowinson.healthcheck.application.interfaces.UserRepository;

public class UserApi {
  public static void attachHandlers(Router router, MySQLPool pool) {
    UserRepository userRepository = new MySQLUserRepository(pool);
    UserApplication userApplication = new UserApplication(userRepository);

    router.post(ApiServerVerticle.PREFIX + "/users").handler(new CreateUserHandler(userApplication));
  }
}
