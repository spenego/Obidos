#!/bin/bash

########################################################################
# print java version on stdout. exit with 1 on error 0 on success
# Adapted from:
# https://stackoverflow.com/questions/7334754/correct-way-to-check-java-version-from-bash-script
########################################################################
if type -p java >/dev/null; then
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    _java="$JAVA_HOME/bin/java"
else
    exit 1
fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ -z $version ]]; then
        echo "Could not determine java version"
        exit 1
    fi
    echo "$version"
    exit 0
fi
