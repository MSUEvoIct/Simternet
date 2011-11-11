CLASSPATH=$SIMTERNET_DIR/bin:
CLASSPATH=$CLASSPATH:$SIMTERNET_DIR/../ECJ/bin
CLASSPATH=$CLASSPATH:$SIMTERNET_DIR/../Mason/bin
CLASSPATH=$CLASSPATH:$SIMTERNET_DIR/lib/log4j-1.2.16.jar
export CLASSPATH

if [ -z "$SIMTERNET_DIR" ]; then
	echo "\$SIMTERNET_DIR must be set to the root of the Simternet project."
	exit 1;
fi
