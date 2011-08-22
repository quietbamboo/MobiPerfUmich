#!/bin/bash
#this script needs to be run from the current directory

for i in Downlink Uplink Collector Version Whoami
#removed bt btnodft and http because apache server will need to be run
do
	echo "running $i"
	cd server/$i

	sudo java -jar $i.jar &

	cd ../../
done
#ps -au | grep "./down.o" | awk '{system("kill "  $2);}'

#reach

cd server/Reach
for i in 21 25 53 110 135 139 143 161 445 465 585 587 993 995 5060 6881 5223 5228 8080
#iphone port 5223 and Android port 5228 and HTTP PROXY 8080
#Bug: smb(445) netbios(139) can't be started
#https is removed because falcon will enable https, 3gtest client will need to modify https response check code
#HTTP / SSH / HTTPS(443) ignored
do
	sudo java -jar Reach.jar $i tcp &
	sudo java -jar Reach.jar $i udp &
done

#start UDP for the special 3 ports
for i in 22 80 443
do
	sudo java -jar Reach.jar $i udp &
done

cd ../..
