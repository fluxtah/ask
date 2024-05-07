#!/bin/bash

# Default Java command
JAVA_CMD="java -jar PATH_TO_JAR"

# Check if the --test-plugin argument is present
if [[ " $@ " =~ " --test-plugin " ]]; then
    # Modify the command to start the Java debugger
    JAVA_CMD="java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar PATH_TO_JAR"
fi

# Execute the command with all passed arguments
$JAVA_CMD "$@"
