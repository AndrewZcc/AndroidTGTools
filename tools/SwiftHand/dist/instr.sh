#!/bin/bash

java -cp "$SCALA_HOME/lib/scala-library.jar:./SwiftHand-all.jar:$JAVA_HOME/lib/tools.jar:$ANDROID_HOME/tools/lib/chimpchat.jar:$ANDROID_HOME/tools/lib/ddmlib.jar:$ANDROID_HOME/tools/lib/ddms.jar:$ANDROID_HOME/build-tools/android-4.4W/lib/dx.jar:$ANDROID_HOME/tools/lib/guava-15.0.jar" edu.berkeley.wtchoi.instrument.CommandLine $1 ~/.android/debug.keystore ./Shared.jar
