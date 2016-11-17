#!/bin/bash

if [ $# -eq 0 ]
  then
    echo "Usage: rm.sh <apkdir>"
    exit
fi

rm $1/*.json $1/*.modified.* $1/*.events $1/*.log $1/*.dex*
rm -r $1/dot
