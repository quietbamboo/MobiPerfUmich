#!/bin/bash
#this script needs to be run from the current directory

ps aux | grep "tcpdump" | awk '{system("sudo kill -9 " $2);}'

for i in Downlink Uplink Collector Version Whoami Tcpdump
do
        echo "stoping $i"
        ps aux | grep "$i.jar" | awk '{system("sudo kill -9 " $2);}'
done

for i in 21 25 53 110 135 139 143 161 445 465 585 587 993 995 5060 6881 22 80 443 5223 5228 8080
#include three special ports, 22, 80 and 443
do
	echo "stoping reach $i"
        ps aux | grep "Reach.jar $i" | awk '{system("sudo kill -9 " $2);}'
done

