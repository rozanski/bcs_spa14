@echo off

rem see http://www.microsoft.com/resources/documentation/windows/xp/all/proddocs/en-us/percent.mspx?mfr=true
set SCRIPTDIR=%~dp0
echo Script directory is %SCRIPTDIR%
set PYTHONPATH=%SCRIPTDIR%;%SCRIPTDIR%\unittest

if X%1==X (
   echo Run the OAuth demo scripts
   echo Usage:
   echo run.bat client [ debug ]
   echo run.bat httpd [ debug ]
   exit /b
)

if X%2==Xdebug (
set OAUTH_DEBUG
)

if X%1==Xclient (
   echo RUNNING OAUTH DEMO CLIENT
   echo =========================
   python -B -i -m oauth_client
   exit /b
)

if X%1==Xhttpd (
   echo RUNNING HTTP SERVER
   echo ===================
   python -B -m oauth_httpd
   exit /b
)

if X%1==Xunittest (
   echo RUNNING PYTHON UNIT TESTS
   echo =========================
   set /p press_enter="Please start the HTTP server and press enter:"
   python -B %SCRIPTDIR%unittest\test_common_oauth.py
   python -B %SCRIPTDIR%unittest\test_dropbox_tools.py
   exit /b
)

