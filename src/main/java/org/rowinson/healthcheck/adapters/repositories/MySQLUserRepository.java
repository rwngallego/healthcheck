package org.rowinson.healthcheck.adapters.repositories;

import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.rowinson.healthcheck.application.interfaces.UserRepository;
import org.rowinson.healthcheck.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the UserRepository and is in charge
 * of handling the DB interactions related to the Users.
 */
public class MySQLUserRepository implements UserRepository {
  private MySQLPool pool;

  private static final Logger LOG = LoggerFactory.getLogger(MySQLServiceRepository.class);

  public MySQLUserRepository(MySQLPool pool) {
    this.pool = pool;
  }

  /**
   * Get all the users (DEMO Only)
   *
   * @return
   */
  @Override
  public Future<ArrayList<User>> GetAllUsers() {
    LOG.info("Getting all the users from DB");

    return SqlTemplate.forQuery(pool,
      "SELECT u.id, u.name FROM users u")
      .mapTo(User.class)
      .execute(Collections.emptyMap())
      .compose(results -> {
        var users = new ArrayList<User>();
        results.forEach(u -> users.add(u));

        LOG.debug("Retrieved: {}", users.toString());
        return Future.succeededFuture(users);
      });
  }

  /**
   * Create the user
   *
   * @param user
   * @return
   */
  public Future<Long> CreateUser(User user) {
    LOG.info("Creating user in DB, name: {}", user.getName());

    Map<String, Object> params = new HashMap<>();
    params.put("name", user.getName());
    return SqlTemplate.forQuery(pool, "INSERT INTO users (name) VALUES (#{name})")
      .execute(params)
      .compose(results -> {
        long id = results.property(MySQLClient.LAST_INSERTED_ID);

        LOG.debug("Created user: {}", id);
        return Future.succeededFuture(id);
      });
  }
}
