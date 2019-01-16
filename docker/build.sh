#!/usr/bin/env bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

docker build -t "vertx-intranet/invoice-microservice" $DIR/../invoice-service
