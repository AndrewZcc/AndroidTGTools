#!/bin/bash

mkdir -p results

export ADK_ROOT=/vagrant/android-sdk-linux
export ADK_LIB=$ADK_ROOT/tools/lib
export CLASSPATH="$ADK_LIB/ddms.jar:$ADK_LIB/ddmlib.jar:$ADK_LIB/chimpchat.jar:$ADK_LIB/guava-15.0.jar:/vagrant/tools/SwiftHand/dist/SwiftHand-all.jar"

if [ ! -z "$1" ]
  then
    apk=`basename $1`
    subj=${apk%.modified.apk}
    echo "Running SwiftHand for $subj $1"
    java edu.berkeley.wtchoi.swift.CommandLine $1 swift 3600 0 results/$subj &> results/$subj.log
    exit 0
fi

echo "Usage: run.sh apk"
exit


echo "Running SwiftHand on all apps"

for i in `ls benchmark/*.apk`; 
do 
  echo "Starting emulator in background"
  emulator -avd swifthand -wipe-data -dns-server 127.0.0.1 &
  echo "Sleeping 5 minutes as emulator starts"
  sleep 300

  x=${i#benchmark/}; 
  subj=${x%.modified.apk}; 
  echo Running SwiftHand for $subj
  java edu.berkeley.wtchoi.swift.CommandLine benchmark/$x swift 3600 0 results/$subj &> results/$subj.log

  echo "Sleeping 2 mins before shutdown"
  sleep 120
  killall emulator64-arm
done

