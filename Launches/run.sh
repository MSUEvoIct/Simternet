java -d64 -Xmx16386M -XX:+UseParallelOldGC -classpath ./bin:../ECJ/bin:../Mason/bin:./lib/log4j-1.2.16.jar ec.Evolve -file ./data/configuration/simternet-ecj.properties -p stat.gather-full=true
