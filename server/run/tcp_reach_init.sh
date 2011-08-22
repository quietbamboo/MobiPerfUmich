#!/bin/bash 
#bash *.sh type device_id run_id port ip

#cd /home/hjx/3gtest

cd data
sudo mkdir $1
sudo mkdir $1/$2
sudo chmod 777 $1
sudo chmod 777 $1/$2
cd ..

#if process already started by new server, ignore the follow parts
ps=$(ps aux | egrep tcpdump$2 | egrep -v "egrep" | awk '{print $0}')

if [ "" = "$ps" ]; then
	cd tcpdump
	td=`which tcpdump`
	sudo cp $td tcpdump$2
	sudo ./tcpdump$2 -i eth0 -s 200 -w ../data/$1/$2/$3_$5_reach.pcap host $5 and ip > /dev/null 2>&1 &
	cd ..
else
	echo -n $ps
fi

echo "tcpdump init ok"
