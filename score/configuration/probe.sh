#!/bin/bash

for ((i=1; i<=30; i++))
do
    curl -s -u "${KIE_SERVER_USER}:${KIE_SERVER_PASSWORD}" http://$(hostname):8080/kie-server/services/rest/server/containers | grep -q SUCCESS
    if [ $? -eq 0 ] ; then
        echo finished
        exit 0
    fi
    sleep 1
done
echo "Failed to connect"
exit 1
