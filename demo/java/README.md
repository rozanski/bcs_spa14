# KEEPING PASSWORDS PRIVATE WITH OAUTH
This directory contains Java code for the BCS SPA 2014
[OAuth session](http://spaconference.org/spa2014/sessions/session576.html).

## Environment
The session should run on Windows, Mac and Linux.

## Prerequisites
### Java
Install **Java 7** if you don't already have it.
You can download Java from the [Oracle Java website](<http://www.oracle.com/technetwork/java/javase/downloads/index.html>).
**You must install the full JDK, not just the JRE** (you will need it for Ant).

For Windows, use Control Panel to set the environment variable `JAVA_HOME` to the location of the JDK
(something like `C:\Progra~1\Java\jdk8`).

### Ant
Install Ant as described here <http://ant.apache.org/manual/install.html>).
There are precompiled binaries on the site.
For Mac you can use macports (run sudo port install apache-ant) - you should download the latest version of macports first.

For Windows, use Control Panel to set the environment variable `ANT_HOME` to the location of the Ant
software (something like `C:\Ant`)
and to add the location of the Ant binary (`%ANT_HOME%\BIN`) to the search path.

### Dropbox Java Library
Install the Dropbox Java library from the [Dropbox website] (<https://www.dropbox.com/developers/core/sdks/java>).

## Building the Demo Software
Run the following command in the java directory:

    ant compile

## Running the Unit Tests
You should run the Java unit tests to make sure everything is installed correctly.

Start the Demo HTTP server as described below, and then in another console run the following command.
For Mac / Linux run:

    ./run.sh unittest

For Windows run one of:

    run.bat unittest

Note that the Dropbox tests take 10 seconds or so to complete.

## Running the Demo
### Starting the Demo Client
Change to the `java` directory and start the client by typing one of the following at the terminal:

    ant -e client
    ant -e client-debug

This will run some initialisation code and display a menu.

### Starting the Demo HTTP Server (Redirect Mode Only)
Open up a second console window, change to the `java` directory 
and start the HTTP server by typing one of the following at the terminal:

    ant -e httpd 
    ant -e httpd-debug

The HTTP server will start and respond to any HTTP requests sent to it.
Test it by browsing to the home page (the HTTP server logs the home page URL to the console in an INFO message).

## Running Demo Tasks
From the client, select one of the redirect menu options to authorise with Dropbox.

Once you have authorised the demo app with Dropbox, you can select options to list, display or create files.
These all call the Dropbox API using the access token you created as part of the
Dropbox authorisation workflow.

