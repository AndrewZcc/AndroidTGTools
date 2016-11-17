#!/bin/bash

SCALA_HOME=/vagrant/scala-2.9.3

for i in `ls /vagrant/subjects/apps/*/*-debug.apk`; do 
  echo Processing $i;
  java -cp "$SCALA_HOME/lib/scala-library.jar:./SwiftHand-all.jar:$JAVA_HOME/lib/tools.jar" edu.berkeley.wtchoi.instrument.CommandLine $i ~/.android/debug.keystore ./Shared.jar > `dirname $i`/swifthand.log
done

