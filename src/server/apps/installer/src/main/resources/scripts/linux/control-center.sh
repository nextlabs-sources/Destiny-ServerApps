#!/bin/bash

export NEXTLABS_CC_HOME="$(
  cd "$(dirname "$(dirname "$0")")"
  pwd -P
)"
export JAVA_HOME="$NEXTLABS_CC_HOME/java/jre"
export ES_JAVA_HOME=$JAVA_HOME
export CATALINA_HOME="$NEXTLABS_CC_HOME/server/tomcat"

if [ -z "$NEXTLABS_CC_USER" ]; then
  export NEXTLABS_CC_USER="nextlabs"
fi

if [ -z "$NEXTLABS_CC_JPDA_OPTS" ]; then
  unset JPDA_OPTS
else
  export JPDA_OPTS=$NEXTLABS_CC_JPDA_OPTS
  export NEXTLABS_CC_JPDA_ARGUMENT=jpda
fi

rm -f "$NEXTLABS_CC_HOME/action-cc-run.txt"
rm -f "$NEXTLABS_CC_HOME/action-cc-es-run.txt"
rm -f "$NEXTLABS_CC_HOME/action-cc-start.txt"
rm -f "$NEXTLABS_CC_HOME/action-cc-installer-run.txt"
rm -f "$NEXTLABS_CC_HOME/action-cc-delete.txt"

if [ "$1" = "-uninstall" ]; then
  "$NEXTLABS_CC_HOME/java/jre/bin/java" -Dlog4j.configurationFile="$NEXTLABS_CC_HOME/server/configuration/log4j2-installer.xml" -Dlogging.config="$NEXTLABS_CC_HOME/server/configuration/log4j2-installer.xml" $JPDA_OPTS -jar "$NEXTLABS_CC_HOME/server/apps/installer.war" "-uninstall" "$@"
elif [ "$2" = "-ui" ]; then
  "$NEXTLABS_CC_HOME/java/jre/bin/java" -Dlog4j.configurationFile="$NEXTLABS_CC_HOME/server/configuration/log4j2-installer.xml" -Dlogging.config="$NEXTLABS_CC_HOME/server/configuration/log4j2-installer.xml" $JPDA_OPTS -jar "$NEXTLABS_CC_HOME/server/apps/installer.war" "-configure-installer" "$@"
  if [ -f "$NEXTLABS_CC_HOME/action-cc-installer-run.txt" ]; then
    rm -f "$NEXTLABS_CC_HOME/action-cc-installer-run.txt"
    export CATALINA_OPTS="-Dlog4j.configurationFile=\"$NEXTLABS_CC_HOME/server/configuration/log4j2-installer.xml\" -Djava.util.logging.config.file=\"$NEXTLABS_CC_HOME/server/tomcat/conf/logging-installer.properties\""
    "$NEXTLABS_CC_HOME/server/tomcat/bin/catalina.sh" $NEXTLABS_CC_JPDA_ARGUMENT run -config "\""$NEXTLABS_CC_HOME/server/configuration/server-installer.xml"\""
    unset CATALINA_OPTS
    rm -f "/etc/ld.so.conf.d/nextlabs-cc-installer-java-libjli.conf"
    ldconfig
    rm -f "$NEXTLABS_CC_HOME/access-key.properties"
    rm -f "$NEXTLABS_CC_HOME/server/configuration/server-installer.xml"
    rm -f "$NEXTLABS_CC_HOME/server/certificates/installer-keystore.jks"
    rm -f "$NEXTLABS_CC_HOME/server/certificates/installer-truststore.jks"
    rm -f "$NEXTLABS_CC_HOME/server/certificates/installer.cer"
    rm -rf "$NEXTLABS_CC_HOME/server/tomcat/installerapp"
  fi
else
  "$NEXTLABS_CC_HOME/java/jre/bin/java" -Dlog4j.configurationFile="$NEXTLABS_CC_HOME/server/configuration/log4j2-installer.xml" -Dlogging.config="$NEXTLABS_CC_HOME/server/configuration/log4j2-installer.xml" $JPDA_OPTS -jar "$NEXTLABS_CC_HOME/server/apps/installer.war" "$@"
fi

if [ -f "$NEXTLABS_CC_HOME/action-cc-run.txt" ]; then
  rm -f "$NEXTLABS_CC_HOME/action-cc-run.txt"
  if [ -f "$NEXTLABS_CC_HOME/action-cc-es-run.txt" ]; then
    rm -f "$NEXTLABS_CC_HOME/action-cc-es-run.txt"
    export ES_HOME="$NEXTLABS_CC_HOME/server/data/search-index"
    echo "NextLabs Control Center Data Indexer starting."
    "$NEXTLABS_CC_HOME/server/data/search-index/bin/elasticsearch" -d -p "\""$NEXTLABS_CC_HOME/server/data/search-index/data/controlcenteres.pid"\""
  fi
  echo "NextLabs Control Center starting."
  "$NEXTLABS_CC_HOME/server/tomcat/bin/catalina.sh" $NEXTLABS_CC_JPDA_ARGUMENT run -config "\""$NEXTLABS_CC_HOME/server/configuration/server.xml"\""
elif [ -f "$NEXTLABS_CC_HOME/action-cc-start.txt" ]; then
  rm -f "$NEXTLABS_CC_HOME/action-cc-start.txt"
  if [ "$2" = "-ui" ]; then
    systemctl start CompliantEnterpriseServer
    systemctl start controlcenterpolicyvalidator
  else
    if [[ "$2" == "-y" ]] || [[ "$2" == "-Y" ]]; then
      export REPLY="y"
    elif [[ "$2" == "-n" ]] || [[ "$2" == "-N" ]]; then
      export REPLY="n"
    else
      read -p "Do you want to start NextLabs Control Center (y - Yes / n - No)? " -r
    fi
    if [[ "$REPLY" == "y" ]] || [[ "$REPLY" == "Y" ]] || [[ "$REPLY" == "yes" ]]; then
      echo "NextLabs Control Center starting."
      systemctl start CompliantEnterpriseServer
      systemctl start controlcenterpolicyvalidator
    fi
  fi
fi
