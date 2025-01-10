#!/bin/bash -ex

docker build . -f Dockerfile.deno -t docker.io/adrianomoreira86/deno-nestjs-app