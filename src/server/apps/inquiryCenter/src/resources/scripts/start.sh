#!/bin/bash

export USER_ID=$(id -u)
export GROUP_ID=$(id -g)
envsubst < /passwd.template > /tmp/passwd
export LD_PRELOAD=/usr/lib64/libnss_wrapper.so
export NSS_WRAPPER_PASSWD=/tmp/passwd
export NSS_WRAPPER_GROUP=/etc/group

$NEXTLABS_CC_HOME/server/tomcat/bin/catalina.sh run -config $NEXTLABS_CC_HOME/server/configuration/server.xml
