# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Java 21 + Spring Boot 3.3.5 Movie Booking System. Uses Maven as the build tool, Spring Data JPA for persistence, and H2 as an in-memory database for development.

## Commands

```bash
mvn clean install              # build and run all tests
mvn spring-boot:run            # start the application (port 8080)
mvn test                       # run all tests
mvn test -Dtest=ClassName      # run a single test class
mvn test -Dtest=ClassName#method  # run a single test method
```

H2 console is available at `http://localhost:8080/h2-console` when the app is running (JDBC URL: `jdbc:h2:mem:moviedb`, user: `sa`, no password).

## Architecture

Root package: `com.moviebooking`

- `MovieBookingApplication` — Spring Boot entry point
- `src/main/resources/application.properties` — datasource, JPA, and H2 console config
- JPA is configured with `ddl-auto=create-drop`, meaning the schema is rebuilt from `@Entity` classes on each startup

The project currently uses H2 in-memory storage; swap `spring.datasource.*` in `application.properties` to migrate to a persistent database (e.g. PostgreSQL).
