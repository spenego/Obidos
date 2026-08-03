#!/bin/bash
# Open up ROOT.war file, fix it and put it back
# Usage: fix_war.sh <pat of ROOT.war> <domain>
# Dec-17-2020 

set -Eeuo pipefail

ARGC=$#
if (( $ARGC != 2 )); then
    echo "$0 <path of ROOT.war> <domain>"
    exit 1
fi
WAR_FILE="$1"
DOMAIN="$2"
CP="/bin/cp -fv"

fixWarFile() {
    local -r warfile="$1"
	local -r domain="$2"
    local -r tdir=$(mktemp -d)

    ${CP} $warfile $tdir/ROOT.war

    pushd $tdir

    # open up ROOT.war
    unzip -q ROOT.war

	local -r webxml=WEB-INF/web.xml

    echo "Fixing $WAR_FILE ..."
    # replace domain and add same site comment
    sed -i.bak 's/$//' $webxml
    sed -i.back '/SAME_SITE_STRICT/d' $webxml
    sed -i.bak "/<cookie-config>/,/<\/cookie-config>/ s/<domain>.*<\/domain>/<domain>$domain<\/domain>\\
        <comment>__SAME_SITE_STRICT__<\/comment>/g;" $webxml

    # update ROOT.war with updated web.xml
	zip -q -r -u ROOT.war WEB-INF
    # replace the original WAR file
    mv ROOT.war $WAR_FILE
    /bin/rm -rf $tdir
    popd
    echo "$WAR_FILE updated"
}
fixWarFile $WAR_FILE $DOMAIN
