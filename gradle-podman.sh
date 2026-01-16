#!/usr/bin/env bash
set -euo pipefail

podman run --rm \
  -v "${PWD}":/app \
  -w /app \
  gradle:8.7-jdk17 \
  gradle "$@"
