#!/bin/bash
#DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Indicate the path of the java compiler to use
#export JAVA_HOME=/usr/csshare/pkgs/jdk1.7.0_17
#export PATH=$JAVA_HOME/bin:$PATH

# compile the java program
javac -d $DIR/../classes $DIR/ProfNetwork.java


