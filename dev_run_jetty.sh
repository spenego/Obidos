#!/bin/bash
# Gemini Pro
# Dec-26-2025 
java --version
if (( $? != 0 )); then
    echo ""
    echo "**** JDK is old, does not support --version. aborting ..."
    echo "**** JAVA_HOME must be set accordingly ***"
    echo ""
    exit 1
fi
sleep 2

#!/bin/bash
# ---------------------------------------------------------------------------
# Spring/Jetty Backend Launcher for JDK 17+
# ---------------------------------------------------------------------------

echo "🚀 Starting Spring Backend on Port 8080..."

# Allow GWT to inspect JDK internals (Critical for RPC Exceptions)
# 1. java.lang: Fixes 'detailMessage' access in Exceptions
# 2. java.util: Fixes serialization of ArrayList/HashMap internals
# 3. java.io: Fixes file/stream serialization issues (often needed)
# 4. java.naming/com.sun.jndi.ldap: Spring LDAP's AbstractContextSource
#    reflectively touches com.sun.jndi.ldap.LdapCtxFactory; jetty:run shares
#    this JVM (via MAVEN_OPTS) so it needs the same opens/exports already
#    declared for maven-surefire-plugin in pom.xml, or it throws
#    IllegalAccessError on the first LDAP authenticate() call.
export MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED \
                   --add-opens java.base/java.util=ALL-UNNAMED \
                   --add-opens java.base/java.io=ALL-UNNAMED \
                   --add-opens java.naming/com.sun.jndi.ldap=ALL-UNNAMED \
                   --add-exports java.naming/com.sun.jndi.ldap=ALL-UNNAMED"

# Run Jetty
# works on intelmac
mvn -f pom.xml war:exploded \
    jetty:run \
    -Djna.library.path=/usr/local/spenego/lib | grep --line-buffered -v "scanned from multiple locations"
#mvn -f pom.xml jetty:run -Djna.library.path=/usr/local/spenego/lib



#mvn -f pom_gwt_dev_jdk11.xml jetty:run -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dhttps.protocols=TLSv1.2
