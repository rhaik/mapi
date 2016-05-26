#!/bin/sh

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR/..

cd .. 

mvn clean

mvn install

cd -

mvn jetty:run
