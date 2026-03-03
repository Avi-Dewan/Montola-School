#!/bin/bash
# prod-run-local.sh

# Set the active profile to 'prod'
export SPRING_PROFILES_ACTIVE=prod

# Load production environment variables from .env.prod
set -a
source .env.prod
set +a

echo "Starting application in PROD mode connected to Neon DB..."

# Run Spring Boot with Gradle
./gradlew bootRun
