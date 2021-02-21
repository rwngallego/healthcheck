package org.rowinson.healthcheck.application.interfaces;

import io.vertx.core.Future;
import org.rowinson.healthcheck.domain.User;

/**
 * Interface for the DB access related to users
 */
public interface UserRepository {
  public Future<User> GetByUsernameAndPassword(String username, String password);
  public Future<Long> CreateUser(User user);
}
