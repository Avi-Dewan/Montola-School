#!/bin/sh
# Starts the application, using the CDS archive when the build produced one.
set -e

CDS=""
if [ -f /app/cds/application.jsa ]; then
    CDS="-XX:SharedArchiveFile=/app/cds/application.jsa"
fi

# JAVA_OPTS is intentionally unquoted so its options are split into arguments.
# shellcheck disable=SC2086
exec java $CDS $JAVA_OPTS -jar /app/app.jar
