# Flatflex

Flatflex is a Java and Spring Boot application created to scrap flat offers from offer sites.

## Tech
- [Spring Boot]
- [postgreSQL]
- [Docker]
- [Spring-Kafka]
- [Apache Avro]
- [Selenium]
- [Redis]
- [Lombok]

## Description

Project developed to improve the process of looking for a flat. 
The goal is to scrap new market offers with flats from different sites and send them via Kafka to the [Data-Dispatcher](https://github.com/wiktorpagacz/data-dispatcher).


## Installation

Prerequisites: installed Java 17 or above, Docker and running [Data-Dispatcher](https://github.com/wiktorpagacz/data-dispatcher).

### Local running (via IntelliJ with Docker container for db etc.)

1. The application is working with [Data-dispatcher](https://github.com/wiktorpagacz/data-dispatcher) running otherwise you have to change default implementation which send data to Kafka topic - comment out @Scheduled annotation above the sendOffersByKafka method in [OfferSender](src/main/java/com/pagacz/flatflex/application/scheduler/OfferSender.java) service.

2. The project isn't full working application you need to create FirstSelector enum at [first directory](src/main/java/com/pagacz/flatflex/infrastructure/first) and prepare selector for [FirstScrapperService](src/main/java/com/pagacz/flatflex/infrastructure/first/FirstScrapperServiceImpl.java)

3. Pull the repository and open the [docker/local/local.env](docker/local/local.env) file inside docker directory, copy file and paste in same place named as .env, set the variables.
4. Build the project by executing in the terminal:
   ```sh
   ./gradlew clean build
   ```
   If the build failed open the [application.properties](src/main/resources/application.properties) file and instead of variables just pass value to database configuration.
5. Set the environment variables to the docker container in the [local docker directory](/docker/local). You can just duplicate local.evn file but rename is as .env and set variables inside.
6. Create a new Run Configuration in IntelliJ
   - add the environment variable from below or install [devEnv](https://plugins.jetbrains.com/plugin/7861-envfile) plugin and load [local.env](docker/local/local.env) file:
     - SPRING_DATASOURCE_USERNAME
     - SPRING_DATASOURCE_PASSWORD
     - A_API
   - VM options: `-Dspring.profiles.active=local`
   - Classpath to run: `data-dispatcher.gateway.run`
   - Name of class with main: `com.pagacz.flatflex.application.FlatflexApplication`
7. Launch the terminal and navigate to the
    ```sh
    docker/local
    ```
8. Make sure to have running Docker application and execute a command in the terminal:
   ```sh
   docker-compose up -d
   ```
   It will run docker container in detached mode
9. If the container for the database and other components runs successfully, you can now run the application through the IDE


[Spring Boot]: <https://github.com/spring-projects/spring-boot>
[Apache Avro]: <https://avro.apache.org>
[Spring-Kafka]: <https://github.com/spring-projects/spring-kafka>
[Docker]: <https://www.docker.com>
[postgreSQL]: <https://www.postgresql.org>
[Redis]: <https://redis.io>
[Lombok]: <https://projectlombok.org>
[Selenium]: <https://www.selenium.dev>