#!/bin/bash
# run.sh

# Load env variables from .env
export $(grep -v '^#' .env | xargs)

# Run Spring Boot with Gradle
./gradlew bootRun