# mood-tracker

[![Build Status](https://travis-ci.org/rvbabilonia/mood-tracker.svg)](https://travis-ci.org/rvbabilonia/mood-tracker)

## Usage

### Database

The `mood-tracker-mysql` module has been compiled and tested on Gradle 6.2.1 and Amazon Corretto 11.0.4.

1. Start `mood-tracker-mysql` Docker container running MySQL 8.0.19.
    ```shell script
    mood-tracker\mood-tracker-mysql\docker\release>docker-compose up
    ```
2. Create the `mood` table using Liquibase.
    ```shell script
    mood-tracker>gradlew :mood-tracker-mysql:update
    ```

### Application Programming Interface

The `mood-tracker-service` module has been compiled and tested on Gradle 6.2.1 and Amazon Corretto 11.0.4.

1. Compile the `mood-tracker-service` module.
    ```shell script
    mood-tracker>gradlew :mood-tracker-service:build
    ```
2. Optionally check if the backend service can run.
    ```shell script
    mood-tracker>gradlew :mood-tracker-service:bootRun
    ```
3. Start `mood-tracker-service` Docker container.
   ```shell script
   mood-tracker\mood-tracker-service\docker\release>docker-compose up
   ```
4. Go to `http://localhost:8080/api/v1/swagger-ui-custom.html` to view the Swagger documentation.

### Web Interface

The `mood-tracker-web` module has been compiled and tested on Node 13.9.0, NPM 6.14.1 and Angular CLI 7.3.9.
There is a known issue with latest NG2-Charts 2.3.0 library so 2.2.3 is used instead.

1. Optionally check if frontend can run.
    ```shell script
    mood-tracker\mood-tracker-web>npm start
    ```
2. Create a production build.
    ```shell script
    mood-tracker\mood-tracker-web>ng build --prod
    ```
3. Start `mood-tracker-web` Docker container.
    ```shell script
    mood-tracker\mood-tracker-web\docker\release>docker-compose up
    ```
4. Go to `http://localhost:4200`.

### Optional Application Performance Management

1. Start `mood-tracker-apm` Docker container running Elasticsearch, Elastic APM server and Kibana.
    ```shell script
    mood-tracker\mood-tracker-apm\docker\release>docker-compose up
    ```
2. Edit `mood-tracker\mood-tracker-service\docker\release\Dockerfile`. Comment out `CMD ["java", "-jar", "/app/app.jar"]`
and uncomment the APM related commands.
3. Recompile the `mood-tracker-service` module.
    ```shell script
    mood-tracker>gradlew :mood-tracker-service:build
    ```
4. Rebuild `mood-tracker-service` Docker container.
   ```shell script
   mood-tracker\mood-tracker-service\docker\release>docker-compose up --build
   ```
5. Go to `http://localhost:5601/app/kibana#/home/tutorial/apm`, check the APM Server status and the agent status then
load the Kibana objects. 
   
## Out of Scope

Due to time constraints, the following features/tasks were not completed:

* Aspect-oriented programming for logging with AspectJ and SLF4J
* Component tests with WireMock
* Contract tests with Pact
* Visual regression tests with Selenium
* Service registration with Netflix Eureka
* API gateway, routing and client authentication with Netflix Zuul
* Formatting with Spotless and Checkstyle
* Frontend unit tests with Karma
* Frontend end-to-end tests with Protractor
