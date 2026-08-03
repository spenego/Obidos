#!/bin/bash
########################################################################
# Compile with any of jdk 11+
# Tested with jdk 11, 17 and 21, requiers GWT 2.12.2 at least
########################################################################

MYDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

initialize() {
    clear
}

iterm_title() {
    echo -ne "\033]0;$1\007"
}

install_jars() {
    ${MYDIR}/install_jars.sh
}

#==================================================================== 
# Compile to create the WAR file
# mode is 'dev' or 'qa' or 'prod'
#==================================================================== 
compile() {
    local -r mode="$1"
    sleep 1
    export MAVEN_OPTS="-Dhttps.protocols=TLSv1.2 \
      --add-opens=java.base/java.lang=ALL-UNNAMED \
      --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
      --add-opens=java.base/java.util=ALL-UNNAMED"
     install_jars
     if [[ "${mode}" == "dev" ]] || [[ "${mode}" == "qa" ]]; then
         echo "🟢 starting dev compilation ..."
         mvn clean package \
             -f pom.xml \
             -Pdev \
             -Dmaven.test.skip=true
     elif [[ "${mode}" == "prod" ]]; then
         echo "🟡 starting production compilation ..."
          mvn clean package \
             -f pom.xml \
             -Dmaven.test.skip=true
     fi
    rc=$?
    echo "Exit code: ${rc}"
    if (( $rc != 0 )); then
        echo "Compilation Failed"
        exit 1
    fi
}

#==================================================================== 
# Alter schema to the latest version
#==================================================================== 
alter_schema() {
    echo ">> Updating schema ...."
    ${MYDIR}/db/alter_schema.sh
}


#==================================================================== 
# Copy the current schema version file to classes so that we can 
# display it in About page
#==================================================================== 
copy_schema_version() {
    /bin/cp -fv ${MYDIR}/db/current_schema_version \
        ${MYDIR}/src/main/resources/schema_version
}

check_java_version() {
    # must be jdk > 1.8
    java --version
    if (( $? != 0 )); then
        echo ""
        echo "**** JDK is old, does not support --version. aborting ..."
        echo "**** Alo JAVA_HOME env var must be set accordingly ***"
        echo ""
        exit 1
    fi
    if [[ -z ${JAVA_HOME} ]]; then
        echo "ERROR: JAVA_HOME env variable is not set. Aborting ..."
        exit 1
    fi
    JAVA_VER=$(${MYDIR}/java_version.sh)
    echo ">> Current Java Version: $JAVA_VER"
    echo ">> JAVA_HOME=${JAVA_HOME}"
}

##-- main--
initialize
if [[ "$1" != "dev" ]] && [[ "$1" != "prod" ]] && [[ "$1" != "qa" ]]; then
    echo "ERROR: Missing argument"
    echo "Usage: $0 [dev|qa|prod]"
    exit 1
fi
MODE="$1"
check_java_version
sleep 2
copy_schema_version
compile ${MODE}
# alter schema only for qa build
if [[ "${MODE}" == "qa" ]]; then
    alter_schema
fi
exit 0

