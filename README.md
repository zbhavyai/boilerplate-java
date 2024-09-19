# Boilerplate Java

Use this boilerplate code to author Java applications using Quarkus.

## Running dev mode

You can run your application in dev mode that enables live coding using below. Dev UI should be accessible at [http://localhost:3005/q/dev-ui/](http://localhost:3005/q/dev-ui/).

```shell
./mvnw compile quarkus:dev
```

## Packaging and running JAR

1. Build the JAR

   ```shell
   ./mvnw package
   ```

2. Run the JAR

   ```shell
   java -jar ./target/boilerplate-java-1.0.0.jar
   ```

## Packaging and running native executable

1. Build native executable

   ```shell
   ./mvnw package -Dnative
   ```

2. Run the executable

   ```shell
   ./target/boilerplate-java-1.0.0
   ```
