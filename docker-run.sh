#!/bin/bash

# Stop and remove the old container if it exists
echo "Cleaning old container..."
docker rm -f montola_app > /dev/null 2>&1

# Build the Docker image with the 'dev' profile
echo "Building the Docker image for DEV..."
docker build --build-arg SPRING_PROFILES_ACTIVE=dev -t montola-app .

# Run the new container
echo "Running the application container..."
docker run -d -p 8080:8080 \
  --name montola_app \
  --network montola-school_default \
  --env-file .env \
  -e POSTGRES_HOST=montola_db \
  -e POSTGRES_PORT=5432 \
  montola-app

# Optional: to view logs
echo "Application container started. To view logs, run: docker logs -f montola_app"
