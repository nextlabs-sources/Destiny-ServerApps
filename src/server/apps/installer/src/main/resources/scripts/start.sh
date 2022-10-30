#!/bin/bash

export USER_ID=$(id -u)
export GROUP_ID=$(id -g)
envsubst < /passwd.template > /tmp/passwd
export LD_PRELOAD=/usr/lib64/libnss_wrapper.so
export NSS_WRAPPER_PASSWD=/tmp/passwd
export NSS_WRAPPER_GROUP=/etc/group

$NEXTLABS_CC_HOME/java/jre/bin/java -Dlog4j.configurationFile=$NEXTLABS_CC_HOME/server/configuration/log4j2-installer.xml -Dlogging.config=$NEXTLABS_CC_HOME/server/configuration/log4j2-installer.xml -Dnextlabs.cc.home=$NEXTLABS_CC_HOME -jar $NEXTLABS_CC_HOME/server/apps/installer.war -run
