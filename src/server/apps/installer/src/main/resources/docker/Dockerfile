FROM registry.access.redhat.com/ubi8/ubi-minimal:8.6-751.1655117800

LABEL name="NextLabs Control Center" \
maintainer="devops@nextlabs.com" \
vendor="NextLabs" \
release="1" \
summary="NextLabs Control Center" \
description="NextLabs Control Center"

RUN microdnf update -y && rm -rf /var/cache/yum
RUN microdnf install -y procps-ng && rm -rf /var/cache/yum  \
    && microdnf install -y shadow-utils iputils && rm -rf /var/cache/yum  \
    && microdnf clean all

ENV NEXTLABS_CC_USER_GROUPID=8888
ENV NEXTLABS_CC_USER_GROUP=nextlabs
ENV NEXTLABS_CC_USER_ID=8888
ENV NEXTLABS_CC_USER_NAME=nextlabs

ENV NEXTLABS_CC_HOME=/opt/NextLabs/PolicyServer

RUN  set -x \
    && groupadd -r -g $NEXTLABS_CC_USER_GROUPID $NEXTLABS_CC_USER_GROUP \
    && useradd -r -u $NEXTLABS_CC_USER_ID -g $NEXTLABS_CC_USER_GROUPID $NEXTLABS_CC_USER_NAME

COPY --chown=${NEXTLABS_CC_USER_NAME}:${NEXTLABS_CC_USER_GROUP} ./PolicyServer ${NEXTLABS_CC_HOME}

RUN  /sbin/setcap CAP_NET_BIND_SERVICE=+eip $NEXTLABS_CC_HOME/java/jre/bin/java \
&& echo "$NEXTLABS_CC_HOME/java/jre/lib/amd64/jli" >> /etc/ld.so.conf.d/nextlabs-cc-java-libjli.conf \
&& ldconfig

RUN mkdir /licenses
COPY licensing.txt /licenses

USER ${NEXTLABS_CC_USER_NAME}

WORKDIR ${NEXTLABS_CC_HOME}

ENTRYPOINT ["./tools/control-center.sh", "-run"]
