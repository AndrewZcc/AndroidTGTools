#!/bin/bash

# Usage
# ./genEmmaCoverageReport.sh [app-name] [ec-file_name]

#echo $0, $1, $2

APPNAME=$1
RESULTDIR=./vagrant/results/guiripper

cp ./subjects/$APPNAME/bin/coverage.em $RESULTDIR/$APPNAME/
cp ./subjects/$APPNAME/bin/coverage.em $RESULTDIR/$APPNAME/output-exp/coverage/

cd $RESULTDIR/$APPNAME/
java -cp /Users/zhchuch/Desktop/paper/Backup/constrast_experi/AndroidTGTools/emma.jar emma report -r html -in coverage.em,$2 -Dreport.html.out.file=./report/coverage.html

# java emma merge -in coverage1.ec,coverage2.ec,coverage3.ec –out coverage.ec
