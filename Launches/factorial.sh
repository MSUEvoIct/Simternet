#!/bin/bash
for i in {1..16}
do
	mv factorial/data.$i data
	bash Launches/run.sh
	mv data factorial/data.$i
done

