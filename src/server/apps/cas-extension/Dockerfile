FROM registry.access.redhat.com/ubi8/ubi-minimal:8.6-751.1655117800

ARG VERSION=latest
ARG APP_DISPLAY_NAME=control-center
ARG BUILD_NUMBER=0

LABEL org.opencontainers.image.title="NextLabs Control Center - ${APP_DISPLAY_NAME}" \
      org.opencontainers.image.description="NextLabs Control Center - ${APP_DISPLAY_NAME} (${VERSION}+${BUILD_NUMBER})" \
      org.opencontainers.image.version="${VERSION}" \
      org.opencontainers.image.vendor="NextLabs" \
      org.opencontainers.image.authors="devops@nextlabs.com"

ENV NEXTLABS_CC_HOME=/opt/nextlabs/control-center

RUN microdnf update -y \
    && microdnf install -y procps-ng hostname nss_wrapper gettext \
    && rm -rf /var/cache/yum  \
    && microdnf clean all

RUN mkdir -p ${NEXTLABS_CC_HOME} \
    && chgrp 0 ${NEXTLABS_CC_HOME} \
    && chmod g+rwX ${NEXTLABS_CC_HOME}

COPY ./control-center ${NEXTLABS_CC_HOME}
COPY ./passwd.template /

WORKDIR ${NEXTLABS_CC_HOME}

RUN ln -s ${NEXTLABS_CC_HOME}/server/certificates/cacerts.jks ${NEXTLABS_CC_HOME}/java/jre/lib/security/cacerts \
    && chmod +x ${NEXTLABS_CC_HOME}/java/jre/bin/java \
    && chmod +x ${NEXTLABS_CC_HOME}/start.sh \
    && chmod +x ${NEXTLABS_CC_HOME}/server/tomcat/bin/catalina.sh

ENTRYPOINT ["./start.sh"]
