#!/usr/bin/env bash
set -ex

./mvnw clean package

docker build . -f ./src/main/docker/Dockerfile.jvm -t docker.io/adrianomoreira86/crud-quarkus-jvm