#!/bin/bash

if [ "$CYGWIN" == "" ]; then
	PATH_SEP=":"
else
	PATH_SEP=";"
	SIMTERNET_HOME=`cygpath -w $SIMTERNET_HOME`
fi


CLASSPATH=${SIMTERNET_HOME}bin
CLASSPATH="${CLASSPATH}${PATH_SEP}${SIMTERNET_HOME}../ECJ/bin"
CLASSPATH="${CLASSPATH}${PATH_SEP}${SIMTERNET_HOME}../Mason/bin"
CLASSPATH="${CLASSPATH}${PATH_SEP}${SIMTERNET_HOME}lib/log4j-1.2.16.jar"

ECJ_FILE="./data/config/evolve-workstation.properties"

JAVA_OPTS="-server -Xmx4096M -XX:ParallelGCThreads=24 -classpath \"$CLASSPATH\""
ECJ_OPTS="-file $ECJ_FILE -p stat.gather-full=true"

echo java $JAVA_OPTS ec.Evolve $ECJ_OPTS

#java -server -Xmx4096M -XX:ParallelGCThreads=24 
#-classpath ./bin:../ECJ/bin:../Mason/bin:./lib/log4j-1.2.16.jar ec.Evolve 
#-file data/config/evolve-workstation.properties -p stat.gather-full=true
#
#
#for i in {1..16}
#do
#	mv factorial/data.$i data
#	bash Launches/run.sh
#	mv data factorial/data.$i
#done

