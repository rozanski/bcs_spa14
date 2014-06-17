#!/bin/bash

function create_skeleton {
    # input line count
    wc1="`wc -l $1 | sed -e 's/ *//' -e 's/ .*//'`"
    echo " processing $filename ($wc1 lines)..."
    extension="${1##*.}"
    if [ "$extension" == py ]; then
        comment='#'
    else
        comment='//'
    fi
    # replace each  block of lines delimited by the START and FINISH tokens
    sed "/SPA14_OAUTH_START/,/SPA14_OAUTH_FINISH/c\\
$comment ==> INSERT CODE HERE
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
rm -rf $exercise_dir/java
rm -rf $exercise_dir/python

# Java setup
echo Creating Java directories...
mkdir -p $exercise_dir/java/files
mkdir -p $exercise_dir/java/jar
mkdir -p $exercise_dir/java/src/oauth_demo/unittest

echo Copying Java files...
cp $demo_dir/java/jar/*.jar $exercise_dir/java/jar
cp $demo_dir/java/files/* $exercise_dir/java/files
cp $demo_dir/java/build.xml $exercise_dir/java/build.xml

echo Processing Java source...
for srcfile in $demo_dir/java/src/oauth_demo/*.java
do
    filename=`basename $srcfile`
    create_skeleton $srcfile $exercise_dir/java/src/oauth_demo/$filename
done
for testfile in $demo_dir/java/src/oauth_demo/unittest/*.java
do
    filename=`basename $testfile`
    create_skeleton $testfile $exercise_dir/java/src/oauth_demo/unittest/$filename
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
