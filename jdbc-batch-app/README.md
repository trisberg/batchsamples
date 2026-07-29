# JDBC Batch App

## build

```sh
./mvnw clean package
```

## run

```sh
java -jar target/jdbc-batch-app-0.1.0.jar \
  --spring.cloud.task.initialize-enabled=true \
  --spring.batch.jdbc.initialize-schema=always
```
## docker

./build-docker.sh
