package org.rowinson.healthcheck;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.micrometer.backends.BackendRegistries;
import org.rowinson.healthcheck.framework.verticles.MainVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the entry point to the application. In this custom launcher
 * the Prometheus metrics/registry are configured
 */
public class CustomLauncher extends Launcher {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    new CustomLauncher().dispatch(args);
  }

  @Override
  public void beforeStartingVertx(VertxOptions options) {
    LOG.info("Starting Vertx");

    // Enable Prometheus metrics
    options.setMetricsOptions(new MicrometerMetricsOptions()
      .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true))
      .setEnabled(true));
  }

  @Override
  public void afterStartingVertx(Vertx vertx) {
    LOG.info("Configuring metrics");

    // Configure the Prometheus registry
    PrometheusMeterRegistry registry = (PrometheusMeterRegistry) BackendRegistries.getDefaultNow();
    registry.config().meterFilter(
      new MeterFilter() {
        @Override
        public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
          return DistributionStatisticConfig.builder()
            .percentiles(0.95, 0.99)
            .build()
            .merge(config);
        }
      });
  }
}
