package org.rowinson.healthcheck.application;

import org.rowinson.healthcheck.application.interfaces.UserRepository;

public class UserApplication {
  UserRepository repo;

  public UserApplication(UserRepository repo) {
   repo = repo;
  }
}
