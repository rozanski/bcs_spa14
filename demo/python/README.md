# KEEPING PASSWORDS PRIVATE WITH OAUTH
This directory contains Python code for the BCS SPA 2014
[OAuth session](http://spaconference.org/spa2014/sessions/session576.html).

## PYTHON INSTALLATION AND SETUP
### Install Python
Install **Python** if you don't already have it.
You can download Python from the [Python website](https://www.python.org/download/).
*Note*: The sample code has only been tested with Python 2.7 (not Python 3.x).

For Windows, add the path to the Python executable (eg `C:\Python27`) to the Windows
PATH environment variable using Windows Control Panel.

### Install pip
Install **pip** if you don't already have it. 
(pip is a tool for downloading and installing packages from PyPI,
"the default package index for the Python community.")
You can find instructions for downloading and installing pip at
<http://pip.readthedocs.org/en/latest/installing.html>.

If you already have pip, you should upgrade to the most recent version. For Mac / Linux, run the following at the command line:

    pip install -U pip

For Windows, run:

    python -m pip install -U pip

### Install Mock
You may need to install Mock to run the unit tests.
You can install Mock using `pip` or `easy_install` (see above).

### Install the Dropbox Python Library
Install the Dropbox Python library.  For Mac / Linux, run the following at the command line:

    pip install dropbox

For Windows, run:

    python -m pip install -U dropbox

### Configure the Demo (Optional)
Edit the file `python/common_oauth.py` if you want to change the browser which is used to run the authorisation workflow.

### Verify Your Setup
You should run the Python unit tests to make sure everything is installed correctly.

Start the Demo HTTP server as described below, and then in another console run the following command.
For Mac / Linux run:

    ./run.sh unittest

For Windows run:

    run.bat unittest

Note that the Dropbox tests take 10 seconds or so to complete.

## RUNNING THE DEMO
### Starting the Demo Client
Change to the `python` directory and start the client.
For Mac / Linux run one of:

    ./run.sh client
    ./run.sh client debug

For Windows run one of:

    run.bat client
    run.bat client debug

This will run some initialisation code, display a menu and and leave you at the Python prompt.

### Starting the Demo HTTP Server (Redirect Mode Only)
Open up a second console window, change to the `python` directory and start the HTTP server.
For Mac / Linux run one of:

    ./run.sh httpd
    ./run.sh httpd debug

For Windows run one of:

    run.bat httpd
    run.bat httpd debug

The HTTP server will start and respond to any HTTP requests sent to it.
Test it by browsing to the home page (the HTTP server logs the home page URL to the console in an INFO message).

## RUNNING DEMO TASKS
The demo includes various functions you can run from the client Python prompt to authorise and interact with Dropbox.
You can authorise the demo app with Dropbox, and then run various commands to list, display or create files.
These all call the Dropbox API using the access token you created as part of the
Dropbox authorisation workflow.

Type `oauth_help()` at any time to display a short help message.

