#!/bin/bash
#
# DATABASE MUST BE POPULATED WITH SCHEMA BEFORE RUNNING THIS.
#
# Before running unit tests, do
#
# sh ./compile.sh dev

# Update:
#  - ignore LDAP test for now
#  Feb-16-2026 

if [[ $# == 1 ]] ; then
    echo "Run Test: $1"
    mvn test "-Dtest=TestObidos#$1"
else
    echo "Run all tests"
     mvn test -Dtest=TestObidos
fi
