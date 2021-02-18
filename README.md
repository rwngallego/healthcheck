# Healthcheck

Monitor the status of service endpoints by running periodic pollers.
It uses [Vert.x](https://vertx.io/), a reactive Java toolkit in the
backend and React in the frontend.

## Documentation

For further information:
- [Design and Architecture](docs/design.md)

## Setup

### Docker container

```bash
docker build -t healthcheck .
docker run healthcheck
```

## Logging

To change the log level `resources/log4j2.xml`:

```xml
    <logger name="org.rowinson.healthcheck" level="info" />
```

## Run
```
./gradlew clean run
```

## Tests
```
./gradlew clean test
```

## Package
```
./gradlew clean assemble
```

