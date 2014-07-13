#!/bin/bash

function create_skeleton {
    echo input file: $1
    echo output file: $2
    # input line count
    wc1="`wc -l $1 | sed -e 's/ *//' -e 's/ .*//'`"
    echo " processing $filename ($wc1 lines)..."
    extension="${1##*.}"
    if [ "$extension" == py ]; then
        comment='# TODO'
    else
        comment='// TODO'
    fi
    # replace each  block of lines delimited by the START and FINISH tokens
    sed "/SPA14_OAUTH_START/,/SPA14_OAUTH_FINISH/c\\
$comment ==> INSERT CODE HERE <==
" $1 > $2
    # output line count
    wc2="`wc -l $2 | sed -e 's/ *//' -e 's/ .*//'`"
    [ "$wc1" -ne "$wc2" ] && echo "   (output contains $wc2 lines)"
}

export exercise_dir=`dirname $0`
export demo_dir=`dirname $0`/../demo

echo Demo Directory is $demo_dir
echo Exercise Directory is $exercise_dir

echo Removing Exercise directories...
rm -rf $exercise_dir/maven
rm -rf $exercise_dir/python

# Java setup
echo Creating Java directories...
mkdir -p $exercise_dir/maven/files
mkdir -p $exercise_dir/maven/jar
mkdir -p $exercise_dir/maven/oauth_demo/src/main/java/uk/org/rozanski/oauth_demo/lib
mkdir -p $exercise_dir/maven/oauth_demo/src/test/java/uk/org/rozanski/oauth_demo/testlib

echo Copying Java files...
cp $demo_dir/maven/jar/*.jar $exercise_dir/maven/jar
cp $demo_dir/maven/files/* $exercise_dir/maven/files

echo Processing Java main source...
for srcfile in $demo_dir/maven/oauth_demo/src/main/java/uk/org/rozanski/oauth_demo/*.java
do
    filename=`basename $srcfile`
    create_skeleton $srcfile $exercise_dir/maven/oauth_demo/src/main/java/uk/org/rozanski/oauth_demo/$filename
done
echo Processing Java library source...
for srcfile in $demo_dir/maven/oauth_demo/src/main/java/uk/org/rozanski/oauth_demo/lib/*.java
do
    filename=`basename $srcfile`
    create_skeleton $srcfile $exercise_dir/maven/oauth_demo/src/main/java/uk/org/rozanski/oauth_demo/lib/$filename
done

echo Processing Java test source...
for srcfile in $demo_dir/maven/oauth_demo/src/test/java/uk/org/rozanski/oauth_demo/*.java
do
    filename=`basename $srcfile`
    create_skeleton $srcfile $exercise_dir/maven/oauth_demo/src/test/java/uk/org/rozanski/oauth_demo/$filename
done
echo Processing Java test library source...
for srcfile in $demo_dir/maven/oauth_demo/src/test/java/uk/org/rozanski/oauth_demo/testlib/*.java
do
    filename=`basename $srcfile`
    create_skeleton $srcfile $exercise_dir/maven/oauth_demo/src/test/java/uk/org/rozanski/oauth_demo/testlib/$filename
done

echo Java complete.

# Python setup
echo creating Python directories...
mkdir -p $exercise_dir/python/files
mkdir -p $exercise_dir/python/unittest

echo Copying Python files...
cp $demo_dir/python/files/* $exercise_dir/python/files
cp $demo_dir/python/run.* $exercise_dir/python

echo Processing Python source...
for srcfile in $demo_dir/python/*.py
do
    filename=`basename $srcfile`
    echo " processing $filename..."
    create_skeleton $srcfile $exercise_dir/python/$filename
done
for testfile in $demo_dir/python/unittest/*.py
do
    filename=`basename $testfile`
    echo " processing unittest/$filename..."
    create_skeleton $testfile $exercise_dir/python/unittest/$filename
done

echo Python complete.
