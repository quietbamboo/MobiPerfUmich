#Compile for MobiPerf server

cd bin

for i in FileThreader LandmarkParser Traverser Gps MultiThreader
#Traverser is not a server, but used to write to mysql
do
	echo -e "Main-Class: analysis."$i"\n" > manifest
	jar cvfm $i.jar manifest analysis/*.class  common/*.class
	scp $i.jar hjx@falcon.eecs.umich.edu:/home/hjx/3gtest/server
done

for i in Tcpdump Downlink Uplink Collector Version Whoami Reach
#Bt BtNondft Http removed   #UserState on hold
do
	#-e: backslash-escaped characters is enabled
	echo -e "Main-Class: servers."$i"\n" > manifest
	jar cvfm $i.jar manifest servers/$i*.class  common/*.class
	scp $i.jar hjx@falcon.eecs.umich.edu:/home/hjx/3gtest/server/$i/
done


rm manifest

cd ..

