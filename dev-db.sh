#!/bin/bash
docker compose -f dev-setup/docker-compose.yml --env-file .env up -d
