package org.rowinson.healthcheck.application;

import io.vertx.core.Future;
import org.rowinson.healthcheck.application.interfaces.UserRepository;
import org.rowinson.healthcheck.domain.User;

import java.util.ArrayList;

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

  /**
   * Get the user by his Id
   * @param userId
   * @return
   */
  public Future<User> getUserById(Long userId) {
    return repo.getUser(userId);
  }

  /**
   * Get all the users from the application.
   * Note: This is for demo purposes and has no results pagination
   * @param offset
   * @param size
   * @param orderBy
   * @param orderAsc
   * @return
   */
  public Future<ArrayList<User>> getAllUsers(int offset, int size, String orderBy, String orderAsc) {
    return repo.getAllUsers();
  }
}
