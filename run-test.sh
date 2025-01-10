#!/usr/bin/env bash
set -e ##bash immediately exit if any command has a non-zero exit status
#set -x ##bash debug mode

#save current directory
WD=$PWD

#define BENCHMARK_ID
export DATE=`date -u +%Y-%m-%d`
export CPU_MODEL_NAME=`lscpu | grep -i "Model name:" | cut -d':' -f2- - | xargs echo -n | sed 's/ /-/g' | sed 's/(R)//g'`
export BENCHMARK_ID="$DATE-$CPU_MODEL_NAME"

# create report output directories
mkdir -p relatorios/$BENCHMARK_ID/k6

#collect system information
BENCHMARK_ID=$BENCHMARK_ID ./dump-system-info.sh

cd ambientes
AMBIENTES=`ls -d */ | cut -f1 -d'/'`
AMBIENTES=js-node-nestjs

for VU in 1 20 60; do
  for AMBIENTE in $AMBIENTES; do
    echo -e "\n\nStarting Test AMBIENTE=$AMBIENTE VU=$VU"
    cd "$WD/ambientes/$AMBIENTE"
    pwd

    #docker compose up -d
    docker stack deploy -c docker-compose.yaml --detach=true `basename $PWD`

    cd $WD

    #wait application start
    #TODO: change for health-check
    sleep 12

    ## warm application with some requests
    ./docker-run-k6.sh run --vus 1 --iterations 1000 ./teste/teste-aquecimento.k6.js
    sleep 2

    ./docker-run-k6.sh run \
                        --env "OUTPUT_FILE=./relatorios/$BENCHMARK_ID/k6/teste-escrita-vu$VU-$AMBIENTE.json" \
                        --summary-trend-stats="med,p(90),p(99),max" \
                        --vus $VU --duration 45s \
                        ./teste/teste-escrita.k6.js
    sleep 2

    ./docker-run-k6.sh run \
                        --env "OUTPUT_FILE=./relatorios/$BENCHMARK_ID/k6/teste-leitura-vu$VU-$AMBIENTE.json" \
                        --summary-trend-stats="med,p(90),p(99),max" \
                        --vus $VU --duration 45s \
                        ./teste/teste-leitura.k6.js
    sleep 2

    ./docker-run-k6.sh run \
                        --env "OUTPUT_FILE=./relatorios/$BENCHMARK_ID/k6/teste-leitura-cache-vu$VU-$AMBIENTE.json" \
                        --summary-trend-stats="med,p(90),p(99),max" \
                        --vus $VU --duration 45s \
                        ./teste/teste-leitura-cache.k6.js

    cd "$WD/ambientes/$AMBIENTE"
    sleep 2
    #docker compose down
    docker stack rm `basename $PWD`
    sleep 10

  done
done

cd $WD

BENCHMARK_ID=$BENCHMARK_ID  node generate-pretty-report.node.js