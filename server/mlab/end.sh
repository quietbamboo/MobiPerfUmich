#!/bin/bash
#this script needs to be run from the current directory

for i in Downlink Uplink KeepAlive
do
        echo "stoping $i"
        ps aux | grep "$i.jar" | awk '{system("sudo kill -9 " $2);}'
done
