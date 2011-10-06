#!/bin/bash
#this script needs to be run from the current directory

for i in Downlink Uplink KeepAlive
do
	echo "running $i"
	java -jar $i.jar &
done

