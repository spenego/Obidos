#!/bin/bash
# Run code server for GWT Dev mode
# Dec-26-2025 
#source $HOME/.jdk11.env
java --version
if (( $? != 0 )); then
    echo ""
    echo "**** JDK is old, does not support --version. aborting ..."
    echo "**** JAVA_HOME must be set accordingly ***"
    echo ""
    exit 1
fi
sleep 2
mvn -Pdev -f pom.xml gwt:codeserver
