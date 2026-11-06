# JDBC Batch App

## build

```sh
./mvnw clean package
```

## run

```sh
java -jar target/batch-app-metrics-0.1.0.jar \
  --spring.cloud.task.initialize-enabled=true \
  --spring.batch.jdbc.initialize-schema=always
```

## run with rsocket proxy

```sh
java -jar target/batch-app-metrics-0.1.0.jar \
  --spring.cloud.task.initialize-enabled=true \
  --spring.batch.jdbc.initialize-schema=always \
  --management.prometheus.metrics.export.enabled=true \
  --micrometer.prometheus.rsocket.enabled=true \
  --micrometer.prometheus.rsocket.host=localhost \
  --micrometer.prometheus.rsocket.port=7001
```

## docker

./build-docker.sh
