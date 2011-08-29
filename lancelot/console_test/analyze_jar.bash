#!/bin/bash

echo $1
mkdir .temp
cd .temp
jar -xf $1 > /dev/null
cd ..
CLASS_FILES=`find .temp -name "*.class"`
echo $CLASS_FILES
bash run_console.bash $CLASS_FILES 
rm -fr .temp
