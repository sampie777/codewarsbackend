#!/bin/sh

# Load secrets
if [ -d "/run/secrets" ]; then
  for path in /run/secrets/*; do
    file="$(basename "${path}")"

    if [[ "$file" = "kubernetes.io" ]]; then
      echo "Skipping file '${file}'"
      continue
    fi

    echo "Exporting secret '${file}' as environment variable"
    export $file=$(cat "${path}") || echo "Failed to export secret '${file}'"
  done
fi

# Run application
echo "Running application..."
java -jar /app/codewarsbackend.jar
