package com.nextlabs.destiny.console.services.impl;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.LoginException;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.services.policy.PolicyEditorStub;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.pf.destiny.services.PolicyEditorClient;
import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.nextlabs.axis.JSSESocketFactoryWrapper;
import com.nextlabs.destiny.console.config.properties.KeyStoreProperties;
import com.nextlabs.destiny.console.config.properties.ServiceUrlProperties;
import com.nextlabs.destiny.console.config.properties.TrustStoreProperties;
import com.nextlabs.destiny.console.dto.policymgmt.PushResultDTO;
import com.nextlabs.destiny.console.model.Component;
import com.nextlabs.destiny.console.repositories.ComponentRepository;
import com.nextlabs.destiny.console.services.DPSProxyService;
import com.nextlabs.destiny.console.services.MessageBundleService;

/**
 * The class <code>DPSProxyServiceImpl</code> implement the interface <code>DPSProxyService</code> which can be used
 * to access DPS service.
 *
 * @author Sachindra Dasun
 */
@Service
public class DPSProxyServiceImpl implements DPSProxyService {

    private static final Logger log = LoggerFactory.getLogger(DPSProxyServiceImpl.class);

    private static final String POLICY_EDITOR_PORT = "/services/PolicyEditor";

    @Value("${cc.home}")
    private String ccHome;

    @Autowired
    private KeyStoreProperties keyStoreProperties;

    @Autowired
    private TrustStoreProperties trustStoreProperties;

    @Autowired
    private ServiceUrlProperties serviceUrlProperties;

    @Autowired
    protected MessageBundleService msgBundle;

    @Autowired
    private ComponentRepository componentRepository;

    private PolicyEditorClient policyEditorClient;

    /**
     * This will set the communication keystore and truststore with core components.
     * Currently this is the only class consume web service from core component.
     * If more classes are created, move this piece of code into common root configuration section.
     */
    @PostConstruct
    public void init() {
        try {
            setSystemProperties();
            URL url = new URL(getDPSUrl());
            ProtocolSocketFactory protocolSocketFactory = new JSSESocketFactoryWrapper();
            Protocol protocol = new Protocol(url.getProtocol(), protocolSocketFactory, url.getPort() == -1 ? 8443 : url.getPort());
            Protocol.registerProtocol(url.getProtocol(), protocol);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setSystemProperties() {
        Path certificatesDir = Paths.get(ccHome, "server", "certificates");
        if (StringUtils.isEmpty(System.getProperty("nextlabs.javax.net.ssl.keyStore"))) {
            System.setProperty("nextlabs.javax.net.ssl.keyStore", certificatesDir.resolve("dcc-keystore.p12").toString());
        }
        if (StringUtils.isEmpty(System.getProperty("nextlabs.javax.net.ssl.keyStorePassword"))) {
            System.setProperty("nextlabs.javax.net.ssl.keyStorePassword", keyStoreProperties.getPassword());
        }
        if (StringUtils.isEmpty(System.getProperty("nextlabs.javax.net.ssl.trustStore"))) {
            System.setProperty("nextlabs.javax.net.ssl.trustStore", certificatesDir.resolve("dcc-truststore.p12").toString());
        }
        if (StringUtils.isEmpty(System.getProperty("nextlabs.javax.net.ssl.trustStorePassword"))) {
            System.setProperty("nextlabs.javax.net.ssl.trustStorePassword", trustStoreProperties.getPassword());
        }
    }

    @Override
    public List<PushResultDTO> schedulePush(Date scheduleTime) {
        List<PushResultDTO> pushResults = new ArrayList<>();
        String dpsUrl = getDPSUrl();
        PushResultDTO pushResult = new PushResultDTO();
        try {
            pushResult.setDpsUrl(dpsUrl);
            if (StringUtils.isNotEmpty(dpsUrl)) {
                Calendar whenCalendar = Calendar.getInstance();
                whenCalendar.setTime(scheduleTime);
                getPolicyEditorService(dpsUrl).schedulePush(whenCalendar);
                pushResult.setSuccess(true);
            }
        } catch (Exception e) {
            pushResult.setMessage(e.getMessage());
            log.warn("Error in sending PUSH request to DPS: " + pushResult.getDpsUrl(), e);
        }
        pushResults.add(pushResult);
        return pushResults;
    }

    public PolicyEditorClient getPolicyEditorClient() {
        if (policyEditorClient == null) {
            IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
            HashMapConfiguration pfClientConfig = new HashMapConfiguration();
            String dpsUrl = getDPSUrl();
            pfClientConfig.setProperty(PolicyEditorClient.LOCATION_CONFIG_PARAM,
                    dpsUrl == null ? null : dpsUrl.substring(0, dpsUrl.lastIndexOf('/')));
            pfClientConfig.setProperty(PolicyEditorClient.USERNAME_CONFIG_PARAM, "dummy");
            pfClientConfig.setProperty(PolicyEditorClient.PASSWORD_CONFIG_PARAM, "dummy!");
            policyEditorClient = compMgr.getComponent(PolicyEditorClient.COMP_INFO, pfClientConfig);
            policyEditorClient.setConfiguration(pfClientConfig);

            try {
                policyEditorClient.login();
            } catch (LoginException | PolicyEditorException e) {
                log.debug(e.toString());
            }
        }
        return policyEditorClient;
    }

    private String getDPSUrl() {
        String dpsUrl = serviceUrlProperties.getDps();
        if (StringUtils.isEmpty(dpsUrl)) {
            dpsUrl = componentRepository.findByType(ServerComponentType.DPS.getName())
                    .map(Component::getComponentUrl)
                    .orElseThrow(NoSuchElementException::new);
        } else {
            dpsUrl = dpsUrl.replaceAll("/$", "");
        }
        return dpsUrl;
    }

    private PolicyEditorStub getPolicyEditorService(String dpsComponentUrl) throws IOException {
        return new PolicyEditorStub(dpsComponentUrl + POLICY_EDITOR_PORT);
    }
}
