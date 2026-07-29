# NoTask Batch App

## build

```sh
./mvnw clean package
```

## run

```sh
java -jar target/notask-batch-app-0.1.0.jar \
  --spring.batch.jdbc.initialize-schema=always
```
## docker

./build-docker.sh
