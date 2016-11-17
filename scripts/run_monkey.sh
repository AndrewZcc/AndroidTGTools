#!/bin/bash


DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
APPDIR=/home/vagrant/subjects/
RESULTDIR=/vagrant/results/`hostname`/monkey/

source $DIR/env.sh
echo "PATH=  $PATH"

cd $APPDIR
for p in `ls -d */`; do
#for p in `cat $DIR/projects.txt`; do

  echo "Setting up AVD"
  cd $DIR
  ./setupEmu.sh android-10

  echo "@@@@@@ Processing project " $p "@@@@@@@"
  mkdir -p $RESULTDIR$p
  cd $APPDIR$p
  echo "** BUILDING APP " $p
  #ant clean
  #ant emma debug install &> $RESULTDIR$p/build.log
  ant installd &> $RESULTDIR$p/install.log
  cp bin/coverage.em $RESULTDIR$p/
  app=`ls bin/*-debug.apk`

  echo "** PROCESSING APP " $app
  package=`aapt d xmltree $app AndroidManifest.xml | grep package | awk 'BEGIN {FS="\""}{print $2}'`

  echo "** RUNNING LOGCAT"
  adb logcat &> $RESULTDIR$p/monkey.logcat &

  echo "** DUMPING INTERMEDIATE COVERAGE "
  cd $DIR
  ./dumpCoverage.sh $RESULTDIR$p &> $RESULTDIR$p/icoverage.log &

  echo "** RUNNING MONKEY FOR" $package
  timeout 1h adb shell monkey -p $package -v 1000000 --throttle 200 --ignore-crashes --ignore-timeouts --ignore-security-exceptions &> $RESULTDIR$p/monkey.log

  echo "-- FINISHED MONKEY"
  adb shell am broadcast -a edu.gatech.m3.emma.COLLECT_COVERAGE
  adb pull /mnt/sdcard/coverage.ec $RESULTDIR$p/coverage.ec

  NOW=$(date +"%m-%d-%Y-%H-%M")
  echo $NOW.$p >> $RESULTDIR/status.log

  killall dumpCoverage.sh
  killall adb

done
