#!/bin/bash
# Destroys the local dev database and recreates it empty.
#
# The next ./dev-run.sh will run Flyway from V1 and the dev seeder will populate
# a small set of demo data. This is the only script here that deletes data, so it
# refuses to run without an explicit confirmation.

set -euo pipefail

if [ "${1:-}" != "--yes" ]; then
    echo "This DESTROYS the local dev Postgres volume (montola_db_data)."
    echo "All local data will be lost. Nothing is backed up."
    echo
    echo "Re-run with --yes to confirm:  ./dev-db-reset.sh --yes"
    exit 1
fi

cd "$(dirname "$0")"

docker compose -f dev-setup/docker-compose.yml --env-file .env down -v
docker compose -f dev-setup/docker-compose.yml --env-file .env up -d

echo
echo "Dev database recreated empty."
echo "Run ./dev-run.sh — Flyway will migrate from V1 and the dev seeder will populate demo data."
