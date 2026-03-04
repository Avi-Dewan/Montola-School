#!/bin/bash
# run.sh

# Set the active profile for local development
export SPRING_PROFILES_ACTIVE=dev

# Load env variables from .env in a more robust way
set -a # automatically export all variables
source .env
set +a # stop automatically exporting

./gradlew clean build

# Run Spring Boot with Gradle
./gradlew bootRun
