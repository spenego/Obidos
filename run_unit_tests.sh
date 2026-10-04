#!/bin/bash
#
# DATABASE MUST BE POPULATED WITH SCHEMA BEFORE RUNNING THIS.
#
# Before running unit tests, do
#
# sh ./compile.sh dev

# Update:
#  - LDAP tests (TestLDAP) are included below. They need a real LDAP
#    test server and credentials, read from env_ldap / env_ldaps /
#    env_ldap_starttls at the project root (see the matching *.example
#    files and install_slapd.txt). Any test whose env file is missing or
#    incomplete skips itself cleanly (JUnit "assume") rather than failing,
#    so this is safe to run without access to that server.
#  Sep-28-2026

if [[ $# == 1 ]] ; then
    echo "Run Test: $1"
    mvn test "-Dtest=TestObidos#$1"
else
    echo "Run all tests"
     mvn test -Dtest=TestObidos,TestLDAP
fi
