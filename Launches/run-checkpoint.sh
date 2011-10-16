#!/bin/bash

java -server -Xmx8192M -XX:ParallelGCThreads=12 -classpath ./bin:../ECJ/bin:../Mason/bin:./lib/log4j-1.2.16.jar simternet.engine.CheckpointRun $1 $2
