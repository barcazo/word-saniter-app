# Stage 1: build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/word-sanitizer-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Set dev profile directly
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]
