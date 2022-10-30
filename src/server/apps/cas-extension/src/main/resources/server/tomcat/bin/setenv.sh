export NEXTLABS_CC_HOME=/opt/nextlabs/control-center
export CATALINA_HOME=$NEXTLABS_CC_HOME/server/tomcat
export JAVA_HOME=$NEXTLABS_CC_HOME/java/jre
export TOMCAT_USER=nextlabs
export CC_HOSTNAME=$(hostname -f)

if [ -z "$NEXTLABS_CC_JAVA_XMS" ]; then
  NEXTLABS_CC_JAVA_XMS="512m"
fi
if [ -z "$NEXTLABS_CC_JAVA_XMX" ]; then
  NEXTLABS_CC_JAVA_XMX="1024m"
fi
if [ -z "$NEXTLABS_CC_PORT_APPSERVICEPORT" ]; then
  export NEXTLABS_CC_PORT_APPSERVICEPORT="8080"
fi
if [ -z "$NEXTLABS_CC_PORT_SERVERSHUTDOWNPORT" ]; then
  export NEXTLABS_CC_PORT_SERVERSHUTDOWNPORT="8005"
fi

export JAVA_OPTS="$NEXTLABS_CC_JAVA_OPTS \
-Xms$NEXTLABS_CC_JAVA_XMS \
-Xmx$NEXTLABS_CC_JAVA_XMX \
-Xverify:none \
-Dcc.home="\""$NEXTLABS_CC_HOME"\"" \
-Dconsole.install.mode=OPN  \
-Djdk.tls.rejectClientInitiatedRenegotiation=true \
-Dlog4j.configurationFile="\""$NEXTLABS_CC_HOME/server/configuration/log4j2.xml"\"" \
-Dlogging.config="\""file:$NEXTLABS_CC_HOME/server/configuration/log4j2.xml"\"" \
-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Jdk14Logger \
-Dorg.springframework.boot.logging.LoggingSystem=none \
-Dserver.config.path="\""$NEXTLABS_CC_HOME/server/configuration"\"" \
-Dserver.hostname="\""${CC_HOSTNAME}"\"" \
-Dorg.apache.tomcat.util.digester.PROPERTY_SOURCE=org.apache.tomcat.util.digester.EnvironmentPropertySource \
-Dorg.apache.tomcat.util.digester.REPLACE_SYSTEM_PROPERTIES=true \
-Dspring.cloud.bootstrap.location="\""$NEXTLABS_CC_HOME/server/configuration/bootstrap.properties"\"" \
-Djava.locale.providers=COMPAT,CLDR \
-Dsun.lang.ClassLoader.allowArraySyntax=true"
