#!/usr/bin/env bash

ssh giga@vps 'mkdir -p daemon' && \
scp ../out/artifacts/Daemon_jar/Daemon.jar giga@vps:daemon/. && \
scp envs/server.xml giga@vps:daemon/env.xml && \
scp server_scripts/restart.sh  giga@vps:daemon/.
ssh giga@vps 'cd daemon && ./restart.sh &'