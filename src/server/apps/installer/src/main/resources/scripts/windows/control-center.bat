@echo off

for %%a in ("%~dp0\.") do for %%b in ("%%~dpa\.") do set NEXTLABS_CC_HOME=%%~dpnxb
if %NEXTLABS_CC_HOME:~-1%==\ SET NEXTLABS_CC_HOME=%NEXTLABS_CC_HOME:~0, -1%

set "JAVA_HOME=%NEXTLABS_CC_HOME%\java\jre"
set ES_JAVA_HOME=%JAVA_HOME%
set "CATALINA_HOME=%NEXTLABS_CC_HOME%\server\tomcat"

if not "%NEXTLABS_CC_JPDA_OPTS%" == "" goto setJpdaOpts
set JPDA_OPTS=
goto okJpda
:setJpdaOpts
set JPDA_OPTS=%NEXTLABS_CC_JPDA_OPTS%
set NEXTLABS_CC_JPDA_ARGUMENT=jpda
:okJpda

del "%NEXTLABS_CC_HOME%\action-cc-es-run.txt" >nul 2>&1
del "%NEXTLABS_CC_HOME%\action-cc-run.txt" >nul 2>&1
del "%NEXTLABS_CC_HOME%\action-cc-start.txt" >nul 2>&1
del "%NEXTLABS_CC_HOME%\action-cc-installer-run.txt" >nul 2>&1
del "%NEXTLABS_CC_HOME%\action-cc-delete.txt" >nul 2>&1

if ""%1"" == ""-uninstall"" goto uninstall

if ""%2"" == ""-ui"" goto installUi
goto install

:installUi
"%NEXTLABS_CC_HOME%\java\jre\bin\java" -Dlog4j.configurationFile="%NEXTLABS_CC_HOME%/server/configuration/log4j2-installer.xml" -Dlogging.config="%NEXTLABS_CC_HOME%/server/configuration/log4j2-installer.xml" %JPDA_OPTS% -jar "%NEXTLABS_CC_HOME%\server\apps\installer.war" "-configure-installer" %*
if not exist "%NEXTLABS_CC_HOME%\action-cc-installer-run.txt" goto installComplete
del "%NEXTLABS_CC_HOME%\action-cc-installer-run.txt"
set CATALINA_OPTS=-Dlog4j.configurationFile="%NEXTLABS_CC_HOME%/server/configuration/log4j2-installer.xml" -Djava.util.logging.config.file="%NEXTLABS_CC_HOME%/server/tomcat/conf/logging-installer.properties"
start /b /wait cmd /c call "%NEXTLABS_CC_HOME%\server\tomcat\bin\catalina.bat" %NEXTLABS_CC_JPDA_ARGUMENT% run  -config "%NEXTLABS_CC_HOME%\server\configuration\server-installer.xml"
set CATALINA_OPTS=
del "%NEXTLABS_CC_HOME%\access-key.properties" >nul 2>&1
del "%NEXTLABS_CC_HOME%\server\configuration\server-installer.xml" >nul 2>&1
del "%NEXTLABS_CC_HOME%\server\certificates\installer-keystore.jks" >nul 2>&1
del "%NEXTLABS_CC_HOME%\server\certificates\installer-truststore.jks" >nul 2>&1
del "%NEXTLABS_CC_HOME%\server\certificates\installer.cer" >nul 2>&1
rmdir /s /q "%NEXTLABS_CC_HOME%/server/tomcat/installerapp" >nul 2>&1
goto installComplete

:install
"%NEXTLABS_CC_HOME%\java\jre\bin\java" -Dlog4j.configurationFile="%NEXTLABS_CC_HOME%/server/configuration/log4j2-installer.xml" -Dlogging.config="%NEXTLABS_CC_HOME%/server/configuration/log4j2-installer.xml" %JPDA_OPTS% -jar "%NEXTLABS_CC_HOME%\server\apps\installer.war" %*

:installComplete
if exist "%NEXTLABS_CC_HOME%\action-cc-start.txt" goto startCc
if exist "%NEXTLABS_CC_HOME%\action-cc-es-run.txt" goto runEs
if exist "%NEXTLABS_CC_HOME%\action-cc-run.txt" goto runCc
goto finish

:runEs
del "%NEXTLABS_CC_HOME%\action-cc-es-run.txt"
set "ES_HOME=%NEXTLABS_CC_HOME%\server\data\search-index"
echo NextLabs Control Center Data Indexer starting.
start /b cmd /c call "%NEXTLABS_CC_HOME%\server\data\search-index\bin\elasticsearch.bat" -d -p "%NEXTLABS_CC_HOME%\server\data\search-index\data\controlcenteres.pid"

:runCc
del "%NEXTLABS_CC_HOME%\action-cc-run.txt"
echo NextLabs Control Center starting.
start /b /wait cmd /c call "%NEXTLABS_CC_HOME%\server\tomcat\bin\catalina.bat" %NEXTLABS_CC_JPDA_ARGUMENT% run -config "%NEXTLABS_CC_HOME%\server\configuration\server.xml"
goto finish

:startCc
del "%NEXTLABS_CC_HOME%\action-cc-start.txt"
if ""%2"" == ""-ui"" goto startCcConfirm
if ""%2"" == ""-y"" goto startCcConfirm
if ""%2"" == ""-Y"" goto startCcConfirm
if ""%2"" == ""-n"" goto exitInstaller
if ""%2"" == ""-N"" goto exitInstaller
set /p NEXTLABS_CC_START_CONFIRMATION="Do you want to start NextLabs Control Center (y - Yes / n - No)? "
if /i "%NEXTLABS_CC_START_CONFIRMATION%" == "y" goto startCcConfirm
if /i "%NEXTLABS_CC_START_CONFIRMATION%" == "Y" goto startCcConfirm
if /i "%NEXTLABS_CC_START_CONFIRMATION%" == "yes" goto startCcConfirm
goto finish
:startCcConfirm
echo NextLabs Control Center starting.
sc start CompliantEnterpriseServer
sc start controlcenterpolicyvalidator.exe
if ""%2"" == ""-ui"" goto exitInstaller
if ""%2"" == ""-y"" goto exitInstaller
if ""%2"" == ""-Y"" goto exitInstaller
goto finish

:uninstall
"%NEXTLABS_CC_HOME%\java\jre\bin\java" -Dlog4j.configurationFile="%NEXTLABS_CC_HOME%/server/configuration/log4j2-installer.xml" -Dlogging.config="%NEXTLABS_CC_HOME%/server/configuration/log4j2-installer.xml" %JPDA_OPTS% -jar "%NEXTLABS_CC_HOME%\server\apps\installer.war" "-uninstall" %*
goto exitInstaller

:finish
pause
:exitInstaller
