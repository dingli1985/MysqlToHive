#!/bin/sh

tpid=$(cat ./tpids)
ex=$(pwdx $tpid|grep `pwd`)

if [ -n "$ex" ]; then
    echo 'Kill Process...,please wait!'
    kill -15 $tpid
    while [ -n "$ex" ]
    do
        sleep 1
        ex=$(pwdx $tpid|grep `pwd`)
    done
    echo 'Kill done!'
else
    echo 'Process not exist!'
fi


