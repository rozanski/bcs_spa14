#!/bin/bash
export PYTHONPATH="${PYTHONPATH:-}:`dirname $0`:`dirname $0`/unittest"

run_help_and_exit() {
      cat <<EOF
Run the OAUth demo scripts
Usage: 
    `basename $0` client [ debug ] # run the interpreter and start the client
    `basename $0` httpd  [ debug ] # start the HTTP daemon and leave it running
    `basename $0` unittest         # run unit tests
    `basename $0` pydoc            # generate documentation for all modules
If second parameter is 'debug' then log debug as well as info messages
EOF
    exit 1
}

if [ $# == 0 ]; then run_help_and_exit; fi

cd `dirname $0`

script="$1"
shift
if [ "$1" == debug ]; then
    export OAUTH_DEBUG=1
fi

if [ "$script" == client ]; then
    echo 'OAUTH DEMO CLIENT (Python interpreter)'
    echo '======================================'
    python -B -i -m oauth_client
elif [ "$script" == httpd ]; then
    echo RUNNING HTTP SERVER
    echo ===================
    python -B -m oauth_httpd
elif [ "$script" == unittest ]; then
    echo RUNNING PYTHON UNIT TESTS
    echo =========================
    read -p 'Please start the HTTP server and press enter: '
    pwd="`pwd`"
    testdir="`dirname $0`/unittest"
    for module in $testdir/test_*.py
    do
        echo TESTING MODULE `basename $module .py`...
        python -B -m "`basename $module .py`"
        echo ''
    done
elif [ "$script" == pydoc ]; then
    echo GENERATING MODULE DOCUMENTATION
    echo ===============================
    docdir="`dirname $0`/doc"
    rm $docdir/*
    for module in ./*.py; do pydoc $module > "$docdir/"`basename $module .py`.txt; done
    epydoc --html  -v --output="$docdir" --show-imports --name=bcs_spa_2014 --url 'http://localhost:55510/home' ./*.py
    rm *.pyc
    echo Documentation files can be found in $docdir
else
    echo invalid option $script
    run_help_and_exit
fi

