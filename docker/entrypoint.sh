#!/bin/sh

# Load secrets
if [ -d "/run/secrets" ]; then
  for path in /run/secrets/*; do
    file="$(basename "${path}")"
    echo "Exporting secret '${file}' as environment variable"
    export $file=$(cat "${path}")
  done
fi

# Run application
java -jar /app/codewarsbackend.jar
