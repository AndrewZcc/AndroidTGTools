#!/bin/bash

echo "\nAndroidRipper: Information Collector\n"

if [ $# != 2 ]; then
	echo "[Parameters Number Error]"
	echo "[Usage: sh collectInfo.sh AUT_Dir AUT_PackName]"
	exit 1
fi

AUT_Dir=$1
AUT_PackageName=$2

echo "--------------------"
echo "Copy ec directory (files)"
echo "Copy ==> ""/data/data/"$AUT_PackageName"/"
adb pull "/data/data/"$AUT_PackageName"/" "./"$AUT_PackageName"/"

echo "\n--------------------"
echo "Copy em file"
echo "Copy ==> /subjects/"$AUT_Dir"/bin/coverage.em"
cp "../../../subjects/"$AUT_Dir"/bin/coverage.em" "./"$AUT_PackageName"/"

echo "\n--------------------"
echo "END ..."