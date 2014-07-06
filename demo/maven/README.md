# KEEPING PASSWORDS PRIVATE WITH OAUTH
This directory contains Java code for the BCS SPA 2014
[OAuth session](http://spaconference.org/spa2014/sessions/session576.html).

## JAVA INSTALLATION AND SETUP
### Install Java
Install **Java 8** if you don't already have it.
You can download Java from the [Oracle Java website](<http://www.oracle.com/technetwork/java/javase/downloads/index.html>).
**You must install the full JDK, not just the JRE**.

For Windows, use Control Panel to set the environment variable `JAVA_HOME` to the location of the JDK
(something like `C:\Progra~1\Java\jdk8`).

### Install Maven
Install Maven as described here: <http://maven.apache.org/download.cgi>.
There are installation instructions for Windows, Mac and Linux.

There are some Maven setup instructions here: <http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html>.

### Install the Dropbox Java Library
Install the Dropbox Java library from the [Dropbox website] (<https://www.dropbox.com/developers/core/sdks/java>).

### Build the Demo Software
Run the following command in the java directory:

    maven clean compile

### Verify your Setup
You should run the Java unit tests to make sure everything is installed correctly.
Run one of the following commands at the terminal:

    maven test
    maven test -Ddebug='debug'

Note that the Dropbox tests take 10 seconds or so to complete.

**HERE!!**

## RUNNING THE DEMO
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

## RUNNING DEMO TASKS
From the client, select one of the redirect menu options to authorise with Dropbox.

Once you have authorised the demo app with Dropbox, you can select options to list, display or create files.
These all call the Dropbox API using the access token you created as part of the
Dropbox authorisation workflow.

