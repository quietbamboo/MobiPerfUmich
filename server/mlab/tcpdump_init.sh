#!/bin/bash 
#bash *.sh type device_id run_id port ip

td=tcpdump-$1-$2-$3-$4
cd ~/mobiperf

cd data
sudo mkdir $1
sudo chmod 777 $1
sudo mkdir $1/$2
sudo chmod 777 $1/$2
cd ..

#if process already started by new server, ignore the follow parts
ps=$(ps aux | egrep $td | egrep -v "egrep" | awk '{print $0}')

if [ "" = "$ps" ]; then
	cd tcpdump
	sudo cp /usr/sbin/tcpdump $td
	sudo ./$td -i eth0 -s 200 -w ../data/$1/$2/$3_$4_$5.pcap port $4 and host $5 and ip > /dev/null 2>&1 &
	cd ..
else
	echo -n $ps
fi

echo "tcpdump init ok"
