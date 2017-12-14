#!/bin/bash

start() {
  cd /home/vagrant/WebGui && npm run watch-poll > /tmp/npm-watch.log &
  echo $! > /tmp/npm-watch-pid
}

stop() {
  kill `cat /tmp/npm-watch-pid` && \
  rm -f /tmp/npm-watch-pid && \
  rm -f /tmp/npm-watch.log
}

case $1 in
  start|stop) "$1" ;;
esac