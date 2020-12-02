#!/bin/sh

java -jar -Dspring.config.location=../config/application.yml ../lib/MysqlToHive-1.0-SNAPSHOT.jar > /dev/null 2>&1 &

echo $! > tpids

echo Start Success!