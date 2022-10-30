package com.nextlabs.destiny.cc.installer.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.JOptCommandLinePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;

import com.nextlabs.destiny.cc.installer.config.properties.Version;
import com.nextlabs.destiny.cc.installer.enums.Component;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.exceptions.InstallerException;
import com.nextlabs.destiny.cc.installer.helpers.CommandLineOptionsHelper;
import com.nextlabs.destiny.cc.installer.helpers.EncryptionHelper;
import com.nextlabs.destiny.cc.installer.helpers.XmlFileHelper;

/**
 * Configure property sources used in Control Center installer.
 *
 * @author Sachindra Dasun
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PropertySourceConfig implements EnvironmentPostProcessor, Ordered {


    private static final Logger logger = LoggerFactory.getLogger(PropertySourceConfig.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String ccHome = environment.getProperty("NEXTLABS_CC_HOME");
        Properties properties = getPropertiesFromFile(ccHome, "../../version.txt");
        if (!properties.isEmpty()) {
            environment.getPropertySources().addFirst(new PropertiesPropertySource("cc-version-properties",
                    properties));
        }
        if (CommandLineOptionsHelper.getOptionSet() != null) {
            environment.getPropertySources().addFirst(new JOptCommandLinePropertySource(CommandLineOptionsHelper.getOptionSet()));
        }
        addApplicationProperties(environment);
        addServerXmlProperties(environment);
        addConfigurationXmlProperties(environment);
    }

    private Properties getPropertiesFromFile(String ccHome, String fileName) {
        Properties properties = new Properties();
        Path propertyFilePath = Paths.get(ccHome, "server", "configuration", fileName);
        if (propertyFilePath.toFile().exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(propertyFilePath.toFile())) {
                properties.load(fileInputStream);
            } catch (IOException e) {
                logger.error(String.format("Error in loading properties from %s", propertyFilePath));
            }
        }
        return properties;
    }

    private void addApplicationProperties(ConfigurableEnvironment environment) {
        if (RunningMode.UPGRADE.name().equals(System.getProperty("nextlabs.cc.running-mode"))) {
            Properties applicationProperties = getPropertiesFromFile(System.getProperty("nextlabs.cc.previous-home"),
                    "application.properties");
            Properties mappedApplicationProperties = new Properties();
            applicationProperties.forEach((key, value) -> {
                if ("db.password".equals(key) && StringUtils.isNotEmpty(value.toString())
                        && !value.toString().startsWith("{cipher}")) {
                    value = String.format(EncryptionHelper.CIPHER_VALUE_FORMAT, value);
                }
                mappedApplicationProperties.put(String.format("nextlabs.cc.%s", key), value);
            });
            if (!mappedApplicationProperties.isEmpty()) {
                environment.getPropertySources().addFirst(new PropertiesPropertySource("cc-previous-application-properties",
                        mappedApplicationProperties));
            }
        }
    }

    private static void addServerXmlProperties(ConfigurableEnvironment environment) {
        if (RunningMode.UPGRADE.name().equals(System.getProperty("nextlabs.cc.running-mode"))) {
            Path serverXmlFilePath = Paths.get(System.getProperty("nextlabs.cc.previous-home"), "server",
                    "configuration", "server.xml");
            Version previousVersion = new Version(System.getProperty("nextlabs.cc.previous-version"));
            if (serverXmlFilePath.toFile().exists()) {
                Map<String, String> xPathToKeyMappings = new HashMap<>();
                xPathToKeyMappings.put("/Server/Service[@name='CE-Config']/Connector/@port", "nextlabs.cc.port.configServicePort");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Core']/Connector/@port", "nextlabs.cc.port.webServicePort");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Apps']/Connector/@port", "nextlabs.cc.port.appServicePort");
                xPathToKeyMappings.put("/Server/Service/Connector/@keystorePass", "nextlabs.cc.ssl.keystore.password");
                xPathToKeyMappings.put("/Server/Service/Connector/@truststorePass", "nextlabs.cc.ssl.truststore.password");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Core']/Engine/Host/Context[@path='/dcsf']/Parameter[@name='DMSLocation']/@value", "nextlabs.cc.managementServer.dmsUrl");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Core']/Engine/Host/Context[@path='/dcsf']/Parameter[@name='Location']/@value", "nextlabs.cc.dcsfLocation");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Apps']/Engine/Host/Context[@path='/administrator']/Parameter[@name='Location']/@value", "nextlabs.cc.administratorLocation");

                xPathToKeyMappings.put("/Server/Service[@name='CE-Config']/Engine/Host/Context[@path='/config-service']/@path", "nextlabs.cc.contextPath.configService");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Core']/Engine/Host/Context[@path='/dms']/@path", "nextlabs.cc.contextPath.dms");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Core']/Engine/Host/Context[@path='/dcsf']/@path", "nextlabs.cc.contextPath.dcsf");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Core']/Engine/Host/Context[@path='/dabs']/@path", "nextlabs.cc.contextPath.dabs");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Core']/Engine/Host/Context[@path='/dac']/@path", "nextlabs.cc.contextPath.dac");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Core']/Engine/Host/Context[@path='/dem']/@path", "nextlabs.cc.contextPath.dem");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Core']/Engine/Host/Context[@path='/dkms']/@path", "nextlabs.cc.contextPath.dkms");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Core']/Engine/Host/Context[@path='/dps']/@path", "nextlabs.cc.contextPath.dps");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Apps']/Engine/Host/Context[@path='/administrator']/@path", "nextlabs.cc.contextPath.administrator");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Apps']/Engine/Host/Context[@path='/']/@path", "nextlabs.cc.contextPath.appHome");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Apps']/Engine/Host/Context[@path='/cas']/@path", "nextlabs.cc.contextPath.cas");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Apps']/Engine/Host/Context[@path='/console']/@path", "nextlabs.cc.contextPath.console");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Apps']/Engine/Host/Context[@path='/policy-controllers']/@path", "nextlabs.cc.contextPath.policyControllers");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Apps']/Engine/Host/Context[@path='/reporter']/@path", "nextlabs.cc.contextPath.reporter");
                xPathToKeyMappings.put("/Server/Service[@name='CE-Apps']/Engine/Host/Context[@path='/services']/@path", "nextlabs.cc.contextPath.services");
                try {
                    Map<String, String> valuesFromXml = XmlFileHelper.readValues(serverXmlFilePath, xPathToKeyMappings);
                    if (!valuesFromXml.isEmpty()) {
                        String keyStorePassword = valuesFromXml.get("nextlabs.cc.ssl.keystore.password");
                        if (StringUtils.isNotEmpty(keyStorePassword)) {
                            valuesFromXml.put("nextlabs.cc.ssl.keystore.password", EncryptionHelper.decrypt(keyStorePassword));
                        }
                        String trustStorePassword = valuesFromXml.get("nextlabs.cc.ssl.truststore.password");
                        if (StringUtils.isNotEmpty(trustStorePassword)) {
                            valuesFromXml.put("nextlabs.cc.ssl.truststore.password", EncryptionHelper.decrypt(trustStorePassword));
                        }
                        String dmsUrl = valuesFromXml.get("nextlabs.cc.managementServer.dmsUrl");
                        if (StringUtils.isNotEmpty(dmsUrl)) {
                            URL url = new URL(dmsUrl);
                            valuesFromXml.put("nextlabs.cc.managementServer.host", url.getHost());
                            valuesFromXml.put("nextlabs.cc.managementServer.webServicePort", String.valueOf(url.getPort()));
                        }
                        String dcsfLocation = valuesFromXml.get("nextlabs.cc.dcsfLocation");
                        if (StringUtils.isNotEmpty(dcsfLocation)) {
                            valuesFromXml.put("nextlabs.cc.serviceName", new URL(dcsfLocation).getHost());
                        }
                        String administratorLocation = valuesFromXml.get("nextlabs.cc.administratorLocation");
                        if (StringUtils.isNotEmpty(administratorLocation)) {
                            valuesFromXml.put("nextlabs.cc.dnsName", new URL(administratorLocation).getHost());
                        }
                        Set<Component> components = new HashSet<>();
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.configService"))) {
                            components.add(Component.CONFIG_SERVICE);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.dms"))) {
                            components.add(Component.DMS);
                            components.add(Component.CONFIG_SERVICE);
                            if(previousVersion.before(Version.V_2022_01)) {
                                components.add(Component.APP_HOME);
                                components.add(Component.POLICY_CONTROLLER_MANAGER);
                                components.add(Component.SERVICE_MANAGER);
                            }
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.dcsf"))) {
                            components.add(Component.DCSF);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.dabs"))) {
                            components.add(Component.DABS);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.dac"))) {
                            components.add(Component.DAC);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.dem"))) {
                            components.add(Component.DEM);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.dkms"))) {
                            components.add(Component.DKMS);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.dps"))) {
                            components.add(Component.DPS);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.administrator"))) {
                            components.add(Component.ADMINISTRATOR);
                        }
                        if (previousVersion.before(Version.V_2022_03)
                                || valuesFromXml.get("nextlabs.cc.contextPath.appHome") != null) {
                            components.add(Component.APP_HOME);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.cas"))) {
                            components.add(Component.CAS);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.console"))) {
                            components.add(Component.CONSOLE);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.policyControllers"))) {
                            components.add(Component.POLICY_CONTROLLER_MANAGER);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.reporter"))) {
                            components.add(Component.REPORTER);
                        }
                        if (StringUtils.isNotEmpty(valuesFromXml.get("nextlabs.cc.contextPath.services"))) {
                            components.add(Component.SERVICE_MANAGER);
                        }
                        if (components.isEmpty()) {
                            throw new InstallerException("Unable to find components from server.xml");
                        } else {
                            valuesFromXml.put("nextlabs.cc.type", StringUtils.join(components, ","));
                        }
                        valuesFromXml.keySet().removeAll(valuesFromXml.keySet().stream()
                                .filter(key -> key.startsWith("nextlabs.cc.contextPath")).collect(Collectors.toList()));
                        environment.getPropertySources().addFirst(new MapPropertySource("cc-server-xml-properties",
                                new HashMap<>(valuesFromXml)));
                    }
                } catch (Exception e) {
                    throw new InstallerException(e);
                }
            }
        }
    }

    private void addConfigurationXmlProperties(ConfigurableEnvironment environment) {
        if (RunningMode.UPGRADE.name().equals(System.getProperty("nextlabs.cc.running-mode"))) {
            Path configurationXmlFilePath = Paths.get(System.getProperty("nextlabs.cc.previous-home"), "server",
                    "configuration", "configuration.xml");
            if (configurationXmlFilePath.toFile().exists()) {
                Map<String, String> xPathToKeyMappings = new HashMap<>();
                xPathToKeyMappings.put("/DestinyConfiguration/Repositories/ConnectionPools/ConnectionPool[1]/ConnectString", "nextlabs.cc.db.url");
                xPathToKeyMappings.put("/DestinyConfiguration/Repositories/ConnectionPools/ConnectionPool[1]/Username", "nextlabs.cc.db.username");
                xPathToKeyMappings.put("/DestinyConfiguration/Repositories/ConnectionPools/ConnectionPool[1]/Password", "nextlabs.cc.db.password");
                try {
                    Map<String, String> valuesFromXml = XmlFileHelper.readValues(configurationXmlFilePath,
                            xPathToKeyMappings);
                    if (!valuesFromXml.isEmpty()) {
                        String dbPassword = valuesFromXml.get("nextlabs.cc.db.password");
                        if (StringUtils.isNotEmpty(dbPassword)) {
                            valuesFromXml.put("nextlabs.cc.db.password", EncryptionHelper.decrypt(dbPassword));
                        }
                        environment.getPropertySources().addFirst(new MapPropertySource("cc-configuration-xml-properties",
                                new HashMap<>(valuesFromXml)));
                    }
                } catch (Exception e) {
                    throw new InstallerException(e);
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
