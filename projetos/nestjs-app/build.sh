#!/usr/bin/env bash
set -ex

docker build . -t docker.io/adrianomoreira86/node-nestjs-app

docker build . -f Dockerfile.bun -t docker.io/adrianomoreira86/bun-nestjs-app