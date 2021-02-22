package org.rowinson.healthcheck.application.interfaces;

import io.vertx.core.Future;
import org.rowinson.healthcheck.domain.User;

import java.util.ArrayList;

/**
 * Interface for the DB access related to users
 */
public interface UserRepository {
  Future<ArrayList<User>> GetAllUsers();

  Future<Long> CreateUser(User user);
}
