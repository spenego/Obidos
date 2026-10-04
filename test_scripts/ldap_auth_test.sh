#!/bin/bash
########################################################################
# ldap_auth_test.sh
#
# Exercises the same two-step LDAP authentication pattern used by
# LDAPSecurity.java: bind with a service account, search for a user by
# a given attribute (e.g. sAMAccountName, uid), then attempt a second
# bind as that user's own DN with the user's password to verify it.
#
# Requires: OpenLDAP client tools (ldapsearch, ldapwhoami)
# Oct-04-2026 
########################################################################

set -uo pipefail

PROG=$(basename "$0")

usage() {
    cat <<EOF
Usage: $PROG -H <ldap_url> -b <base_dn> -D <bind_dn> -a <attribute> -u <username> [options]

Required:
  -H <ldap_url>     LDAP server URL, e.g. ldap://ad.example.com:389 or ldaps://ad.example.com:636
  -b <base_dn>      Search base DN, e.g. "dc=example,dc=com"
  -D <bind_dn>      Service/bind account DN used to search the directory
  -a <attribute>    Attribute to match the username against, e.g. sAMAccountName, uid
  -u <username>     Username to look up and authenticate

Optional:
  -w <bind_pass>    Password for -D. If omitted, you will be prompted (hidden input).
  -p <user_pass>    Password for the user being authenticated. If omitted, you will
                     be prompted (hidden input).
  -Z                Use StartTLS (equivalent to "Start TLS" in the Obidos LDAP config).
                     Requires -H to use an "ldap://" URL, not "ldaps://".
  -r                Do NOT escape LDAP filter metacharacters in the username (raw).
                     Use this to test filter-injection payloads, e.g.:
                       -u '*)(|(objectClass=*'
  -t <seconds>      Network/search timeout in seconds (default: 5)
  -v                Verbose: print the ldapsearch/ldapwhoami commands being run
                     (passwords are never printed).
  -h                Show this help and exit

Examples:
  # Plain LDAP, simple bind, prompt for both passwords
  $PROG -H ldap://ad.example.com:389 -b "dc=example,dc=com" \\
        -D "cn=svc-obidos,dc=example,dc=com" -a sAMAccountName -u jdoe

  # StartTLS, passwords supplied on the command line
  $PROG -H ldap://ad.example.com:389 -Z -b "dc=example,dc=com" \\
        -D "cn=svc-obidos,dc=example,dc=com" -w 'BindP@ss' \\
        -a sAMAccountName -u jdoe -p 'UserP@ss'

  # Filter-injection smoke test (expect this to NOT authenticate as an
  # arbitrary directory entry if the app-side fix is working)
  $PROG -H ldap://ad.example.com:389 -Z -b "dc=example,dc=com" \\
        -D "cn=svc-obidos,dc=example,dc=com" -w 'BindP@ss' \\
        -a sAMAccountName -u '*)(|(objectClass=*' -p 'anything' -r
EOF
}

LDAP_URL=""
BASE_DN=""
BIND_DN=""
BIND_PASS=""
ATTR=""
USERNAME=""
USER_PASS=""
USE_STARTTLS=0
RAW_FILTER=0
TIMEOUT=5
VERBOSE=0
BIND_PASS_SET=0
USER_PASS_SET=0

while getopts "H:b:D:w:a:u:p:Zrt:vh" opt; do
    case "$opt" in
        H) LDAP_URL="$OPTARG" ;;
        b) BASE_DN="$OPTARG" ;;
        D) BIND_DN="$OPTARG" ;;
        w) BIND_PASS="$OPTARG"; BIND_PASS_SET=1 ;;
        a) ATTR="$OPTARG" ;;
        u) USERNAME="$OPTARG" ;;
        p) USER_PASS="$OPTARG"; USER_PASS_SET=1 ;;
        Z) USE_STARTTLS=1 ;;
        r) RAW_FILTER=1 ;;
        t) TIMEOUT="$OPTARG" ;;
        v) VERBOSE=1 ;;
        h) usage; exit 0 ;;
        *) usage; exit 1 ;;
    esac
done

missing=()
[[ -z "$LDAP_URL" ]] && missing+=("-H ldap_url")
[[ -z "$BASE_DN"  ]] && missing+=("-b base_dn")
[[ -z "$BIND_DN"  ]] && missing+=("-D bind_dn")
[[ -z "$ATTR"     ]] && missing+=("-a attribute")
[[ -z "$USERNAME" ]] && missing+=("-u username")

if (( ${#missing[@]} > 0 )); then
    echo "Error: missing required argument(s): ${missing[*]}" >&2
    echo >&2
    usage >&2
    exit 1
fi

if ! command -v ldapsearch >/dev/null 2>&1; then
    echo "Error: ldapsearch not found in PATH" >&2
    exit 1
fi
if ! command -v ldapwhoami >/dev/null 2>&1; then
    echo "Error: ldapwhoami not found in PATH" >&2
    exit 1
fi

if (( BIND_PASS_SET == 0 )); then
    read -r -s -p "Bind password for $BIND_DN: " BIND_PASS
    echo
fi
if (( USER_PASS_SET == 0 )); then
    read -r -s -p "Password for user $USERNAME: " USER_PASS
    echo
fi

# RFC 4515 filter escaping: backslash must be escaped first.
escape_ldap_filter() {
    local s="$1"
    s="${s//\\/\\5c}"
    s="${s//\*/\\2a}"
    s="${s//\(/\\28}"
    s="${s//\)/\\29}"
    printf '%s' "$s"
}

if (( RAW_FILTER == 1 )); then
    FILTER_VALUE="$USERNAME"
    echo "WARNING: -r given, username is NOT filter-escaped (injection test mode)" >&2
else
    FILTER_VALUE=$(escape_ldap_filter "$USERNAME")
fi

SEARCH_FILTER="(${ATTR}=${FILTER_VALUE})"

COMMON_OPTS=(-x -H "$LDAP_URL" -o "nettimeout=${TIMEOUT}")
if (( USE_STARTTLS == 1 )); then
    # -ZZ: require StartTLS to succeed, hard-fail otherwise. A single -Z only
    # warns and silently falls back to plaintext on failure, which would let
    # this test "PASS" over an unencrypted connection without saying so.
    COMMON_OPTS+=(-ZZ)
fi

echo "=== Step 1: bind as service account and search for user ==="
echo "  LDAP URL   : $LDAP_URL"
echo "  Base DN    : $BASE_DN"
echo "  Bind DN    : $BIND_DN"
echo "  StartTLS   : $([[ $USE_STARTTLS == 1 ]] && echo yes || echo no)"
echo "  Filter     : $SEARCH_FILTER"

SEARCH_CMD=(ldapsearch "${COMMON_OPTS[@]}" -D "$BIND_DN" -w "$BIND_PASS" -b "$BASE_DN" -s sub "$SEARCH_FILTER" dn)

if (( VERBOSE == 1 )); then
    printf '  + %q ' "${SEARCH_CMD[@]}" | sed 's/-w [^ ]*/-w ***/'
    echo
fi

SEARCH_OUTPUT=$("${SEARCH_CMD[@]}" 2>&1)
SEARCH_RC=$?

if (( SEARCH_RC != 0 )); then
    echo
    echo "FAIL: search bind/query failed (rc=$SEARCH_RC)"
    echo "$SEARCH_OUTPUT"
    exit 1
fi

USER_DN=$(echo "$SEARCH_OUTPUT" | awk '/^dn: / {print substr($0,5); exit}')

if [[ -z "$USER_DN" ]]; then
    echo
    echo "FAIL: no entry found matching $SEARCH_FILTER under $BASE_DN"
    exit 1
fi

echo "  Found DN   : $USER_DN"

echo
echo "=== Step 2: bind as the found user DN with the supplied password ==="

WHOAMI_CMD=(ldapwhoami "${COMMON_OPTS[@]}" -D "$USER_DN" -w "$USER_PASS")

if (( VERBOSE == 1 )); then
    printf '  + %q ' "${WHOAMI_CMD[@]}" | sed 's/-w [^ ]*/-w ***/'
    echo
fi

WHOAMI_OUTPUT=$("${WHOAMI_CMD[@]}" 2>&1)
WHOAMI_RC=$?

echo
if (( WHOAMI_RC == 0 )); then
    echo "PASS: authenticated successfully as $USER_DN"
    echo "  $WHOAMI_OUTPUT"
    exit 0
else
    echo "FAIL: could not authenticate as $USER_DN (invalid credentials or bind rejected)"
    echo "$WHOAMI_OUTPUT"
    exit 1
fi
