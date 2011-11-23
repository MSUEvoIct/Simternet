#!/bin/bash

if [ -z "$SIMTERNET_DIR" ]; then
	echo "\$SIMTERNET_DIR must be set to the root of the Simternet project."
	exit 1;
fi

. ${SIMTERNET_DIR}/scripts/stuff.sh

java -Xmx8192M -XX:ParallelGCThreads=24 ec.Evolve -file ${SIMTERNET_DIR}/etc/$1.properties -p stat.gather-full=true
