#!/usr/bin/env bash
set -ex

./mvnw clean package

docker build . -t docker.io/adrianomoreira86/crud-spring-jvm-reactive
