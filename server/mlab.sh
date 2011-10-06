#!/bin/bash
#Compile and deploy for MLab servers
if [ $1 = "-c" ]; then
	mkdir mlab

	cd bin

	for i in Downlink Uplink KeepAlive
	do
		#-e: backslash-escaped characters is enabled
		echo -e "Main-Class: servers."$i"\n" > manifest
		jar cvfm $i.jar manifest servers/$i*.class  common/*.class
		mv $i.jar ../mlab/
	done

	rm manifest

	cd ..

elif [ $1 = "-d" ]; then
	for n in `cat mlab/nodeList`
	do
		ping=`ping -c 2 -W 2 $n | grep " 0.0\% packet loss" | wc -l`
		if [ $ping = "1" ]; then
			echo $n " on"
		else
			echo $n " off"
			continue
		fi
		echo "Deploy"
		if [ $2 = "-e" ]; then
			ssh -p 806 -l michigan_1 $n 'cd ~/mobiperf;bash end.sh'
		else
			ssh -p 806 -l michigan_1 $n 'mkdir ~/mobiperf'
			scp -P 806  mlab/* michigan_1@$n:~/mobiperf
			ssh -p 806 -l michigan_1 $n 'cd ~/mobiperf;bash start.sh'
		fi
		exit
	done
else
	echo "Usage: compile -c; deploy -d; terminate -d -e"
fi
