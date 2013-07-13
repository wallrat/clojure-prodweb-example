#!/bin/bash

# create a PID unique filename
export WORKDIR=.$(basename $0).$$
export JAR=app.jar

# each process gets it's own wordir
mkdir -p $WORKDIR

# make a hardlink to the jar
ln -f $0 $WORKDIR/$JAR

# cleanup workdir on exit
trap "cd ..; rm -fr $WORKDIR" EXIT

# run the linked jar
cd $WORKDIR
java -jar $JAR $*
exit
