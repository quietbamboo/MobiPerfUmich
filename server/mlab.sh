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
	for n in `cat nodeList`
	do
	#	if [ $n = "mlab3.atl01.measurement-lab.org" ];then
	#		echo "this one"
	#	else
	#		continue
	#	fi

		ping=`ping -c 2 -W 2 $n | grep " 0.0\% packet loss" | wc -l`
		if [ $ping = "1" ]; then
			echo $n " on"
		else
			echo $n " off"
			continue
		fi
		echo "Deploy"
		if [ $2 = "-e" ]; then
			ssh -o "StrictHostKeyChecking no" -p 806 -l michigan_1 $n 'cd ~/mobiperf;bash end.sh'
		elif [ $2 = "-i" ]; then
			ssh -o "StrictHostKeyChecking no" -p 806 -l michigan_1 $n 'sudo yum -y install java' &
		else
			#ssh -o "StrictHostKeyChecking no" -p 806 -l michigan_1 $n 'mkdir ~/mobiperf' &
			scp -o "StrictHostKeyChecking no" -P 806  mlab/* michigan_1@$n:~/mobiperf
			#ssh -o "StrictHostKeyChecking no" -p 806 -l michigan_1 $n 'cd ~/mobiperf;bash start.sh'
		fi
	done
elif [ $1 = "-t" ];then
        ps aux | grep "measurement-lab.org" | awk '{system("sudo kill -9 " $2);}'
else
	echo "Usage: compile -c; deploy -d; terminate remotely -d -e; install java -d -i; kill all local process -t"
fi
