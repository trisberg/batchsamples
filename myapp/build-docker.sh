#!/usr/bin/env bash
set -euo pipefail

VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
IMAGE_NAME="${IMAGE_NAME:-ghcr.io/trisberg/apps/myapp}:${VERSION}"

echo "Building Docker image: ${IMAGE_NAME} (linux/amd64)"

./mvnw spring-boot:build-image \
  -DskipTests \
  -Dspring-boot.build-image.imageName="${IMAGE_NAME}" \
  -Dspring-boot.build-image.imagePlatform=linux/amd64

echo "Successfully built image: ${IMAGE_NAME}"
