#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"

DCLASSPATH=`find $DIR/../lib/ -name "*.jar" | xargs echo | tr ' ' ':'`

ln -s $DIR/../bin $DIR/bin

JAVA_CMD="/usr/bin/java"
JAVA_OPTS=""
JAVA_CLASSPATH=".:/usr/share/java/js.jar:/usr/share/java/jline.jar:$DCLASSPATH"
JAVA_MAIN="org.mozilla.javascript.tools.shell.Main"

isOpenJDK=`$JAVA_CMD -version 2>&1 | grep -i "OpenJDK" | wc -l`
if [ $isOpenJDK -gt 0 ]
then
	JAVA_OPTS="-Xbootclasspath:/usr/lib/jvm/java-6-openjdk/jre/lib/rt.jar"
fi

$JAVA_CMD $JAVA_OPTS -classpath $JAVA_CLASSPATH \
          $JAVA_MAIN lancelot_console_verifier.rhino.js $DIR $@

rm $DIR/bin          
