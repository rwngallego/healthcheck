package org.rowinson.healthcheck.application;

import io.vertx.core.Future;
import org.rowinson.healthcheck.application.interfaces.UserRepository;
import org.rowinson.healthcheck.domain.User;

public class UserApplication {
  UserRepository repo;

  public UserApplication(UserRepository repo) {
    this.repo = repo;
  }

  /**
   * Creates a new user
   *
   * @param user
   * @return
   */
  public Future<Long> createUser(User user) {
    return repo.createUser(user);
  }

  public Future<User> getUserById(Long userId) {
    return repo.getUser(userId);
  }
}
