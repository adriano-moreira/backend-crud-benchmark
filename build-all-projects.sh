#!/usr/bin/env bash
set -ex

cd projetos

for projeto in *;do
  cd $projeto
  ./build.sh
  cd -
done
