#!/bin/bash
########################################################################
# Plain, unencrypted LDAP bind test. Credentials come from env_ldap at the
# project root (copy env_ldap.example there and fill it in).
# Oct-04-2026 
########################################################################

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/../env_ldap"

if [[ ! -f "$ENV_FILE" ]]; then
    echo "Error: $ENV_FILE not found." >&2
    echo "Copy env_ldap.example to env_ldap at the project root and fill it in." >&2
    exit 1
fi

set -a
source "$ENV_FILE"
set +a

: "${LDAP_URI:?LDAP_URI not set in $ENV_FILE}"
: "${LDAP_BASE_DN:?LDAP_BASE_DN not set in $ENV_FILE}"
: "${LDAP_BIND_DN:?LDAP_BIND_DN not set in $ENV_FILE}"
: "${LDAP_BIND_PASSWORD:?LDAP_BIND_PASSWORD not set in $ENV_FILE}"
: "${LDAP_AUTH_ATTR:?LDAP_AUTH_ATTR not set in $ENV_FILE}"
: "${LDAP_TEST_USERNAME:?LDAP_TEST_USERNAME not set in $ENV_FILE}"
: "${LDAP_TEST_PASSWORD:?LDAP_TEST_PASSWORD not set in $ENV_FILE}"

"$SCRIPT_DIR/ldap_auth_test.sh" -H "$LDAP_URI" \
  -b "$LDAP_BASE_DN" -D "$LDAP_BIND_DN" -w "$LDAP_BIND_PASSWORD" \
  -a "$LDAP_AUTH_ATTR" -u "$LDAP_TEST_USERNAME" -p "$LDAP_TEST_PASSWORD"
