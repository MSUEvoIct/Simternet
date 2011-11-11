#!/bin/bash

if [ -z "$SIMTERNET_DIR" ]; then
SIMTERNET_DIR=.
fi

java -server -Xmx8192M -XX:ParallelGCThreads=24 -classpath $SIMTERNET_DIR/bin:$SIMTERNET_DIR/../ECJ/bin:$SIMTERNET_DIR../Mason/bin:$SIMTERNET_DIR/lib/log4j-1.2.16.jar simternet.engine.CheckpointRun $1 $2
