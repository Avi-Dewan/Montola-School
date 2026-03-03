# Stage 1: Build the application
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Argument to set the active profile, defaults to 'prod' if not provided
ARG SPRING_PROFILES_ACTIVE=prod
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
