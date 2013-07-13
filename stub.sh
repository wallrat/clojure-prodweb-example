#!/bin/bash

# create a PID unique filename
PWD=$(pwd)
export WORKDIR=$PWD/.$(basename $0).$$
export JAR=$WORKDIR/app.jar

# each process gets it's own workdir
mkdir -p $WORKDIR

# make a hardlink to the jar
ln -f $0 $JAR

# cleanup workdir on exit
trap "rm -fr $WORKDIR" EXIT

# run the linked jar
java -jar $JAR $*
exit
