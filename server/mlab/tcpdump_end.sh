#!/bin/bash
#bash *.sh type device_id run_id port ip

td=tcpdump-$1-$2-$3-$4
cd ~/mobiperf

for ps in $(ps aux | grep $td | awk '{print $2}')
do
	echo $ps
	sudo kill $ps
done

#cd /home/hjx/3gtest

rm -f tcpdump/$td


#perl scripts/getdownperf.pl data/$1/$2/$3up.pcap $4  >> data/$1/$2/$3.out

chmod 777 data/$1/$2/* 

echo "tcpdump end ok"
