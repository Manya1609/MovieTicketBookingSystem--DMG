# Stage 1: build
FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2: run
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/movie-booking-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
