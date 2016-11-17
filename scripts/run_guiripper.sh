#!/bin/bash

#DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DIR=/Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools
SCRIPTDIR=$DIR/scripts
APPDIR=$DIR/subjects/
RESULTDIR=$DIR/vagrant/results/guiripper/
export TOOLDIR=$DIR/tools/guiripper

source $SCRIPTDIR/env.sh
echo "PATH=  $PATH"

cd $APPDIR
#for p in `ls -d */`; do
#for p in `cat $SCRIPTDIR/projects3.txt`; do
  p=tippy_1.1.3/
  # AVD setup is performed by GUI Ripper
  #for avd in `android list avd -c`; do
  #  android delete avd -n $avd
  #done
  android delete avd -n gui-ripper
  killall emulator64-x86

  echo "** CREATING EMULATOR"
  #echo no | android create avd -n gui-ripper -t android-10 -c 1024M -b x86 --snapshot
  echo no | android create avd -n gui-ripper -t android-17 -c 1024M -b armeabi-v7a --snapshot

  echo "@@@ Processing project "$p
  mkdir -p $RESULTDIR$p
  cd $APPDIR$p

  app=`ls bin/*-debug.apk`
  apkName=`basename $app`

  echo "** PROCESSING APP " $app
  rm -rf $TOOLDIR/apks/*
  cp bin/*-debug.apk $TOOLDIR/apks/

  echo "============================================================"
  echo "** Preparing Emulator for GUI Ripper with APK" $apkName
  cd $TOOLDIR
  date1=$(date +"%s")
  echo "TOOLDIR = $TOOLDIR";

  bash -x run.sh prepare systematic apks/$apkName > $RESULTDIR$p/tool_prepare.log  # 结果的重定向输出错了, 要 modify &> to >
  
  date2=$(date +"%s")
  diff=$(($date2-$date1))
  echo "Preparation took $(($diff / 60)) minutes and $(($diff % 60)) seconds."
  echo -e "============================================================\n"

  echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
  echo "** DUMPING INTERMEDIATE COVERAGE "
  cd $SCRIPTDIR
  ./dumpCoverage.sh $RESULTDIR$p > $RESULTDIR$p/icoverage.log &
  echo -e "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"

  echo "============================================================"
  echo "** RUNNING GUI RIPPER FOR" $apkname
  cd $TOOLDIR
  date1=$(date +"%s")
  #timeout 1h bash -x run.sh ripper systematic apks/$apkName &> $RESULTDIR$p/tool.log
  gtimeout 480s bash -x run.sh ripper systematic apks/$apkName > $RESULTDIR$p/tool.log
  date2=$(date +"%s")
  diff=$(($date2-$date1))
  echo "Performed ripping for $(($diff / 60)) minutes and $(($diff % 60)) seconds."  
  echo -e "============================================================\n"
  
  echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
  echo "** COPY REPORTS"
  cp -r $TOOLDIR/output-exp $RESULTDIR$p/
  echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
  echo "-- FINISHED GUI RIPPER -- "
  adb shell am broadcast -a edu.gatech.m3.emma.COLLECT_COVERAGE
  adb pull /mnt/sdcard/coverage.ec $RESULTDIR$p/coverage.ec

  NOW=$(date +"%m-%d-%Y-%H-%M")
  echo $NOW.$p >> $RESULTDIR/status.log

  adb kill-server
  rm -r $TOOLDIR/apks/*.apk
  kill -9 `ps | grep dumpCoverage.sh | awk '{print $1}'`
#done
