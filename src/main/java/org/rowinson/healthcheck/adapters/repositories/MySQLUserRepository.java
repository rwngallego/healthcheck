package org.rowinson.healthcheck.adapters.repositories;

import io.vertx.mysqlclient.MySQLPool;
import org.rowinson.healthcheck.application.interfaces.UserRepository;

/**
 * This class implements the UserRepository and is in charge
 * of handling the DB interactions related to the Users.
 */
public class MySQLUserRepository implements UserRepository {
  private MySQLPool pool;

  public MySQLUserRepository(MySQLPool pool) {
    pool = pool;
  }
}
