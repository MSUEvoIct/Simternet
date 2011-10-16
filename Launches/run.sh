#!/bin/bash

if [ -z "$SIMTERNET_DIR" ]; then
SIMTERNET_DIR=.
fi

java -server -Xmx8192M -XX:ParallelGCThreads=24 -classpath $SIMTERNET_DIR/bin:$SIMTERNET_DIR/../ECJ/bin:$SIMTERNET_DIR../Mason/bin:$SIMTERNET_DIR/lib/log4j-1.2.16.jar ec.Evolve -file data/config/evolve-workstation.properties -p stat.gather-full=true
