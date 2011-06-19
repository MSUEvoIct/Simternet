#!/bin/bash

cat ./data/configuration/simternet-ecj.properties > this-run.properties
cat ./data/configuration/bigsim.properties >> this-run.properties
java -d64 -Xmx4096M -XX:+UseParallelOldGC -classpath ./bin:../ECJ/bin:../Mason/bin:./lib/log4j-1.2.16.jar ec.Evolve -file this-run.properties -p stat.gather-full=true
