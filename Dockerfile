# Stage 1: Build the application
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test --no-daemon

# The build emits both an executable jar and a -plain jar; give the executable
# one a stable name so later stages never have to guess which is which.
RUN cp /app/build/libs/*-SNAPSHOT.jar /app/app.jar

# Stage 2: Generate a Class Data Sharing archive to speed up JVM startup.
# Runs on the same base image as the runtime stage so the archive is loadable.
# The training run must not need a database — Flyway, schema validation and pool
# connections are disabled and a throwaway URL is supplied. Failure here is
# non-fatal: the runtime simply starts without the archive.
FROM eclipse-temurin:21-jdk-alpine AS cds
WORKDIR /app
COPY --from=builder /app/app.jar app.jar
RUN mkdir -p /cds \
 && (timeout 180 java \
        -Dspring.context.exit=onRefresh \
        -Dspring.flyway.enabled=false \
        -Dspring.jpa.hibernate.ddl-auto=none \
        -Dspring.datasource.url=jdbc:postgresql://127.0.0.1:5432/cds-training \
        -Dspring.datasource.username=cds \
        -Dspring.datasource.password=cds \
        -Dspring.datasource.hikari.minimum-idle=0 \
        -Dspring.datasource.hikari.initialization-fail-timeout=-1 \
        -XX:ArchiveClassesAtExit=/cds/application.jsa \
        -jar app.jar \
     || echo "CDS training skipped (non-fatal)")

# Stage 3: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Argument to set the active profile, defaults to 'prod' if not provided
ARG SPRING_PROFILES_ACTIVE=prod
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

# JVM options tuned for a small instance (Render free is ~0.1 CPU / 512 MB):
# a single-threaded GC, no C2 profiling at startup, no JMX, no banner.
ENV JAVA_OPTS="-XX:+UseSerialGC -XX:TieredStopAtLevel=1 -Xss512k -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Dspring.jmx.enabled=false -Dspring.main.banner-mode=off -Djava.security.egd=file:/dev/./urandom"

COPY --from=builder /app/app.jar /app/app.jar
COPY --from=cds /cds /app/cds
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["/app/docker-entrypoint.sh"]
