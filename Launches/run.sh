#!/bin/bash

java -server -Xmx4096M -XX:ParallelGCThreads=8 -classpath ./bin:../ECJ/bin:../Mason/bin:./lib/log4j-1.2.16.jar ec.Evolve -file data/config/evolve-workstation.properties -p stat.gather-full=true
