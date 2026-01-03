#!/bin/bash
# run.sh

# Load env variables from .env
export $(grep -v '^#' .env | xargs)

./gradlew clean build

# Run Spring Boot with Gradle
./gradlew bootRun