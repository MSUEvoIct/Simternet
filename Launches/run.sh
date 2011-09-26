#!/bin/bash

java -server -Xmx8192M -XX:ParallelGCThreads=24 -classpath ./bin:../ECJ/bin:../Mason/bin:./lib/log4j-1.2.16.jar ec.Evolve -file data/config/evolve-workstation.properties -p stat.gather-full=true
