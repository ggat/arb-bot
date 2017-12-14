#!/bin/bash

#cd /etc/systemd/system && \
sudo cp files/vs.service /etc/systemd/system/ && \
sudo systemctl daemon-reload && \
mkdir -p /home/vagrant/scripts/startup/ && \
cp -p scripts/* /home/vagrant/scripts/startup/. && \
sudo service vs start