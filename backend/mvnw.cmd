@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Batch Script for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set "APP_HOME=%~dp0"
if "%APP_HOME:~-1%"=="\" set "APP_HOME=%APP_HOME:~0,-1%"

set "MVN_BASE=%APP_HOME%\.mvn\apache-maven-3.9.6"

if not exist "%MVN_BASE%\bin\mvn.cmd" (
    echo Downloading Maven 3.9.6...
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $wc = New-Object System.Net.WebClient; $wc.DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip', '%APP_HOME%\.mvn\maven.zip'); Expand-Archive -Path '%APP_HOME%\.mvn\maven.zip' -DestinationPath '%APP_HOME%\.mvn' -Force; Remove-Item '%APP_HOME%\.mvn\maven.zip' -Force"
)

"%MVN_BASE%\bin\mvn.cmd" %*
