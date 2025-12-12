@echo off
REM Set JAVA_HOME to Java 23.0.1
set JAVA_HOME=C:\Users\Reioz\Downloads\java\openjdk-23.0.1_windows-x64_bin\jdk-23.0.1
set PATH=%JAVA_HOME%\bin;%PATH%

REM Verify Java version
java -version

REM Build the project
mvn clean package

pause

