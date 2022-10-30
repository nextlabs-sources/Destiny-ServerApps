set "CATALINA_HOME=%NEXTLABS_CC_HOME%/server/tomcat"
set "CLASSPATH=%NEXTLABS_CC_HOME%/server/tomcat/shared/lib/nxl-filehandler.jar"
set "JAVA_HOME=%NEXTLABS_CC_HOME%/java/jre"

if not "%NEXTLABS_CC_JAVA_XMS%" == "" goto gotJavaXms
set "NEXTLABS_CC_JAVA_XMS=1024m"
:gotJavaXms
if not "%NEXTLABS_CC_JAVA_XMX%" == "" goto gotJavaXmx
set "NEXTLABS_CC_JAVA_XMX=4096m"
:gotJavaXmx

set JAVA_OPTS=%NEXTLABS_CC_JAVA_OPTS% ^
-Xms%NEXTLABS_CC_JAVA_XMS% ^
-Xmx%NEXTLABS_CC_JAVA_XMX% ^
-Xverify:none ^
-Dcc.home="%NEXTLABS_CC_HOME%" ^
-Dconsole.install.mode=OPN  ^
-Djdk.tls.rejectClientInitiatedRenegotiation=true ^
-Dlog4j.configurationFile="%NEXTLABS_CC_HOME%/server/configuration/log4j2.xml" ^
-Dlogging.config="file:%NEXTLABS_CC_HOME%/server/configuration/log4j2.xml" ^
-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Jdk14Logger ^
-Dorg.springframework.boot.logging.LoggingSystem=none ^
-Dserver.config.path="%NEXTLABS_CC_HOME%/server/configuration" ^
-Dserver.hostname=${CC_HOSTNAME} ^
-Dspring.cloud.bootstrap.location="%NEXTLABS_CC_HOME%/server/configuration/bootstrap.properties" ^
-Djava.locale.providers=COMPAT,CLDR ^
-Dsun.lang.ClassLoader.allowArraySyntax=true

