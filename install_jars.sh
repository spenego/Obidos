#!/bin/bash

########################################################################
# Install local jars to maven repo
# spgdev@spenego.com Nov-28-2022 
# 
# Add pronounceable password generator library
# spgdev@spenego.com Sep-08-2024 #
# Install local jars to maven rep

export MAVEN_OPTS="-Dhttps.protocols=TLSv1.2 \
  --add-opens=java.base/java.lang=ALL-UNNAMED \
  --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
  --add-opens=java.base/java.util=ALL-UNNAMED"
MYDIR=$(dirname $0)
STRONGPASS_VERSION="1.0.2"
mvn install:install-file \
   -Dfile=${MYDIR}/jars/StrongPass-${STRONGPASS_VERSION}.jar \
   -DgroupId=com.spenego.strongpass \
   -DartifactId=StrongPass \
   -Dversion=${STRONGPASS_VERSION} \
   -Dpackaging=jar \
   -DgeneratePom=true

WISEPERSIST_VERSION="1.0.1"
mvn install:install-file \
   -Dfile=${MYDIR}/jars//wisepersist-sdk-${WISEPERSIST_VERSION}.jar \
   -DgroupId=org.wisepersist \
   -DartifactId=wisepersist-sdk \
   -Dversion=${WISEPERSIST_VERSION} \
   -Dpackaging=jar \
   -DgeneratePom=true

GPW_VERSION="1.0.2"
mvn install:install-file \
   -Dfile=${MYDIR}/jars/gpw-${GPW_VERSION}.jar \
   -DgroupId=com.muquit.gpw \
   -DartifactId=gpw \
   -Dversion=${GPW_VERSION} \
   -Dpackaging=jar \
   -DgeneratePom=true
