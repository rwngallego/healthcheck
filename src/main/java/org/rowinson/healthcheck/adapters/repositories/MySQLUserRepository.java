package org.rowinson.healthcheck.adapters.repositories;

import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.rowinson.healthcheck.application.interfaces.UserRepository;
import org.rowinson.healthcheck.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  @Override
  public Future<User> GetByUsernameAndPassword(String username, String password) {
    Map<String, Object> params = new HashMap<>();
    params.put("username", username);
    params.put("password", password);
    return SqlTemplate.forQuery(pool,
      "SELECT u.id, u.username, u.password FROM users u WHERE u.username = #{username} AND u.password = #{password}")
      .mapTo(User.class)
      .execute(params)
      .compose(results -> {
        var result = results.iterator().hasNext() ? results.iterator().next() : null;
        return Future.succeededFuture(result);
      });
  }

  public Future<Long> CreateUser(User user) {
    Map<String, Object> params = new HashMap<>();
    params.put("username", user.getUsername());
    params.put("password", user.getPassword());
    return SqlTemplate.forQuery(pool, "INSERT INTO users (username, password) VALUES (#{username}, #{password})")
      .execute(params)
      .compose(results -> {
        long id = results.property(MySQLClient.LAST_INSERTED_ID);
        return Future.succeededFuture(id);
      });
  }
}
