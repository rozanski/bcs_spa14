# KEEPING PASSWORDS PRIVATE WITH OAUTH
This repository contains code for the [above session](http://spaconference.org/spa2014/sessions/session576.html)
at the 2014 [BCS SPA Conference](http://www.spaconference.org/spa2014/).

The session will will explain how the OAuth security protocol allows
developers to offer cloud services to users without requiring them
to provide their usernames and passwords.
It uses Dropbox as an example cloud service.

## Demo Code
There are two implementations of the code needed for the session, one in Python and one one Java. They are functionally equivalent, so you can use whichever language you prefer.

## Prerequisites
### Dropbox
Sign up to **Dropbox** at <https://www.dropbox.com> if you don't already have an account.
You do not need to install the Dropbox desktop software onto your computer
(if you do, to will also be able to view the demo files in your local Dropbox folder `Dropbox/apps/bcs_spa_2014`).

### Installing the Demo Software
Create a user-owned and writeable directory into which you will install the demo files.

Download the files in my git repository <https://github.com/rozanski/bcs_spa14> into this directory.
You can download the files directly from the website (click _Download Zip_) or retrieve them using a git tool.

You should run the unit tests for your chosen language to make sure everything is installed correctly.
There are more detailed installation and unit test instructions in the READMEs in the `python` and `java` directories.

## How the Demo Works
The demo has two parts:

1. Authorise with Dropbox using OAuth;
1. Run various commands to display or manipulate Dropbox files (to demonstrate that
authorisation was successful).

You will need to run two console sessions:

- one console for the client (which controls the demo);
- a second console to run an HTTP server (which is only used to complete the OAuth "redirect" authorisation).

### Authorisation Modes
The demo client authorises with Dropbox in one of two modes.

- In _no-redirect mode_, the Dropbox authorisation webpage displays an authorisation code
which the user copies and pastes into the client when prompted.
It does not redirect the client to a URL after authorisation.

- In _redirect mode_, the Dropbox authorisation webpage automatically redirects the client
back to another webpage after authorisation (or for client-side apps, to a URI scheme which invokes a program).
No authorisation code is displayed (it is returned as part of the redirect URL).

No-redirect mode is simpler (it does not require a URI handler) but pasting a long code is awkward for the user.
Most real applications which use the OAuth "authorisation token" workflow use redirect mode.

### Access Token
Once you have authorised with Dropbox, the access token is saved to a file on disk
and used in subsequent calls to Dropbox functions.

### Interacting with Dropbox
All interactions with Dropbox in the demo take place over the Internet using the
Dropbox REST API.
The demo does _not_ access any Dropbox files on your computer.

## Running the Demo
The README files in the `python` and `java` folders give instructions on setting up and running the demo.

### Logging Messages
The demo programs log error and information messages to the console.
If you run the programs in debug mode, they also log debug messages.

### Authorising with Dropbox
When running redirect mode, if you are not currently logged in to Dropbox in your browser, you will have to
enter your Dropbox login and password before the authorisation web page is displayed.
(This information is not visible to the client program.)

If you have configured Dropbox two-factor authentication, you may also have to enter a code
which has been sent to your mobile phone.

This is all part of the Dropbox login process, not the OAuth protocol.

### De-Authorising with Dropbox
To de-authorise with Dropbox, runt the command or select the option to delete the access token file.
You will then have to run through either of the authorisation workflows again.

You can authorise and de-authorise any number of times.

### HTTP Server
The demo includes an HTTP server which listens on the local host.
This is used to simulate redirection to a URI scheme.

You need to start the HTTP server before running "the redirect" authorisation workflow.
The HTTP server is only needed while you are authorising with Dropbox in redirect mode.

## Viewing the Demo Documentation
The `doc` folder in the python and java folders contains documentation for the demo code.
You can open the HTML files in a web browser or view them from the home page of the demo HTTP server.

## To Do
I need to create skeleton versions of the demo files which session attendees can populate with code.

__Nick Rozanski__    
June 2014    
<mailto:nick@rozanski.org.uk>    


