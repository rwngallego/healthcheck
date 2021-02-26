package org.rowinson.healthcheck.framework.verticles;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.rowinson.healthcheck.adapters.repositories.MySQLServiceRepository;
import org.rowinson.healthcheck.application.ServiceApplication;
import org.rowinson.healthcheck.domain.Service;
import org.rowinson.healthcheck.framework.Config;
import org.rowinson.healthcheck.framework.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Creates the verticle in charge of executing the polling
 * to the monitored services
 */
public class PollerVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(PollerVerticle.class);

  // message topics
  public static final String MSG_SERVICE_STATUS_SUCCEEDED = "service.status.succeeded";
  public static final String MSG_SERVICE_STATUS_FAILED = "service.status.failed";
  public static final String MSG_SERVICE_DELETED = "service.deleted";
  public static final String MSG_SERVICE_CREATED = "service.created";

  // ms before failing an attempt
  public static final int TIMEOUT_IN_MS = 500;
  // time between retries, this can be extended with exponential backoff
  public static final long LINEAR_BACKOFF = 10L;
  // number of retries before opening the circuit
  public static final int MAX_RETRIES = 2;
  // these ids can be used for a safe and orchestrated shutdown. Ideally
  // we could use a SharedData to distribute across clusters
  private static HashMap<Long, Long> memory = new HashMap<>();
  // how frequent we are polling the services
  public static final int PERIODIC_DELAY_MS = 5000;

  private ServiceApplication serviceApplication;

  /**
   * This will get a DB pool, load the existing services from it
   * and start the service registration logic
   * @param startPromise
   */
  @Override
  public void start(Promise<Void> startPromise) {
    var client = WebClient.create(vertx, new WebClientOptions().setUserAgent("HealthCheck/v0.1"));
    var eb = vertx.eventBus();

    Config.GetValues(vertx)
      .onSuccess(config -> {
        // each worker has access to 1 DB pool
        var pool = Database.GetPool(vertx, config);
        var repo = new MySQLServiceRepository(pool);
        this.serviceApplication = new ServiceApplication(repo);
      })
      .compose(next -> loadFromDB(eb, client))
      .onSuccess(next -> {
        // this will create message consumers for when a service is added/deleted
        handleServiceRegistrations(client);
        startPromise.complete();
      })
      .onFailure(error -> {
        LOG.error("Could not start the worker verticle: ", error);
        startPromise.fail(error);
      });
  }

  /**
   * This will listen to added/deleted services
   * @param client
   */
  private void handleServiceRegistrations(WebClient client) {
    EventBus eb = vertx.eventBus();
    eb.<JsonObject>consumer(MSG_SERVICE_CREATED)
      .handler(m -> {
        var service = m.body().mapTo(Service.class);
        registerService(eb, client, service);
      });
    eb.<Long>consumer(MSG_SERVICE_DELETED)
      .handler(m -> {
        var serviceId = m.body();
        unregisterService(serviceId);
      });
  }

  /**
   * This registers a service in the periodic timer. Each
   * PERIODIC_DELAY_MS the service will be polled using a retry
   * with incremental backoff mechanism that can be adjusted.
   *
   * Note that the timer ids are stored so that they can be deregister when the
   * services are removed.
   *
   * @param eb
   * @param client
   * @param service
   */
  private void registerService(EventBus eb, WebClient client, Service service) {
    vertx.setPeriodic(PERIODIC_DELAY_MS, timerId -> {
      memory.put(service.getId(), timerId);

      LOG.info("Executing polling cycle with id {}", timerId);

      pollOneService(eb, client, service);
    });
  }

  /**
   * This will unregister a service, which means the cancellation
   * of the periodic timer and the removal from the poller memory
   * @param serviceId
   */
  private void unregisterService(Long serviceId) {
    var timerId = memory.get(serviceId);
    if (timerId != null) {
      vertx.cancelTimer(timerId);
    }
    memory.remove(serviceId);
  }

  /**
   * This will execute asynchronously the poll to a
   * service. It uses a retry mechanism with a linear time increment.
   * Note that the circuit is open after MAX_RETRIES.
   * @param eb
   * @param client
   * @param service
   */
  private void pollOneService(EventBus eb, WebClient client, Service service) {
    URL url = getUrl(service);
    if (url == null) return;
    var port = url.getPort();
    var host = url.getHost();
    var endpoint = url.getPath() + url.getQuery();

    // Configurable linear backoff retry mechanism
    var breakerOptions = new CircuitBreakerOptions()
      .setMaxRetries(MAX_RETRIES)
      .setTimeout(TIMEOUT_IN_MS);
    CircuitBreaker breaker = CircuitBreaker.create("poller-linear-backoff", vertx, breakerOptions)
      .retryPolicy(count -> {
        LOG.info("Service {}. Retry: {}", service.getId(), count);
        return count * LINEAR_BACKOFF;
      });

    LOG.info("Service {}. Polling service", service.getId());

    // Execute the http request
    breaker.execute(promise -> {
      LOG.info("Service {}. Executing request", service.getId());

      client.get(port, host, endpoint)
        .send()
        .onFailure(error -> promise.fail(error))
        .onSuccess(response -> {
          if (response.statusCode() == 200) {
            promise.complete("OK");
          } else {
            promise.fail("FAIL");
          }
        });
    }).onSuccess(r -> {
      eb.publish(MSG_SERVICE_STATUS_SUCCEEDED, JsonObject.mapFrom(service));
      LOG.info("Service {}. Succeeded poll, result: {}", service.getId(), r);
    }).onFailure(r -> {
      eb.publish(MSG_SERVICE_STATUS_FAILED, JsonObject.mapFrom(service));
      LOG.info("Service {}. Failed poll, result: {}", service.getId(), r);
    });
  }

  /**
   * This will load the existing services from the DB and for
   * each of them will register them in the polling system.
   * @param eb
   * @param client
   * @return
   */
  private Future<Void> loadFromDB(EventBus eb, WebClient client) {
    LOG.info("Loading existing services from the DB");

    return serviceApplication.getRegisteredServices()
      .compose(services -> {
        services.stream().forEach(service -> {
          registerService(eb, client, service);
        });
        return Future.succeededFuture();
      });
  }

  /**
   * Parses the service url and return a URL object
   * @param service
   * @return
   */
  private URL getUrl(Service service) {
    URL url;
    try {
      url = new URL(service.getUrl());
    } catch (MalformedURLException e) {
      LOG.error("Service {}. Could not parse the URL, reason:", service.toJson(), e);
      return null;
    }
    return url;
  }

}
