#!/usr/bin/env bash
if [ -s pid ]
then
  kill `cat pid`
fi

java -jar Daemon.jar >output.log 2>&1 && \
echo $! > pid