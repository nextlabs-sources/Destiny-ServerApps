package com.nextlabs.destiny.console.services.authentication.impl;

import com.nextlabs.destiny.console.config.properties.KeyStoreProperties;
import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.dto.authentication.ComplexUserAttribute;
import com.nextlabs.destiny.console.dto.authentication.Saml2;
import com.nextlabs.destiny.console.enums.SecureStoreFile;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.InvalidInputParamException;
import com.nextlabs.destiny.console.model.FileResource;
import com.nextlabs.destiny.console.services.FileResourceService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.SecureStoreService;
import com.nextlabs.destiny.console.services.authentication.DelegationResourceService;
import org.apache.commons.io.IOUtils;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProvicerRequestedAttribute;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.util.SAML2HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SAML2 delegation resources involves physical file which is required for the libraries to work
 * To support container deployment behavior, these resource files need to be stored into database
 * and get downloaded to the container upon pod/node starts up.
 *
 * @author Chok Shah Neng
 * @since 2020.09
 */
@Service("Saml2DelegationResourceService")
public class Saml2DelegationResourceServiceImpl
        implements DelegationResourceService {

    private static final Logger log = LoggerFactory.getLogger(Saml2DelegationResourceServiceImpl.class);

    private static final String SERVICE_LOCATION = "%s/cas/login?client_name=saml2";
    private static final String SINGLE_LOGOUT_LOCATION = "%s/cas/login?client_name=saml2&logoutendpoint=true";

    private static final String SP_METADATA_PATH = String.join(File.separator, "${cc.home}", "server", "configuration", "sp-metadata.xml");
    private static final String IDP_METADATA_PATH = String.join(File.separator, "${cc.home}", "server", "configuration", "idp-metadata.xml");
    private static final String LOGIN_IMAGE_PATH = String.join(File.separator, "${cc.home}", "server", "tomcat", "webapps",
                    "cas", "WEB-INF", "classes", "static", "images", "saml2.png");

    @Value("${cc.home}")
    private String controlCenterHome;

    @Value("${server.name}")
    private String serverName;

    @Autowired
    private KeyStoreProperties keyStoreProperties;

    @Autowired
    private SecureStoreService secureStoreService;

    @Autowired
    private FileResourceService fileResourceService;

    @Autowired
    protected MessageBundleService msgBundle;

    /**
     * Generate service provider metadata.xml file and setup signing certificate
     *
     * @param handlerDetail Authentication configuration enter by user
     */
    @Override
    public void configure(AuthHandlerDetail handlerDetail)
            throws ConsoleException {
        File idpMetadata = new File(String.join(File.separator, controlCenterHome, "server", "configuration", "idp-metadata.xml"));
        File spMetadata = new File(String.join(File.separator, controlCenterHome, "server", "configuration", "sp-metadata.xml"));

        try {
            if (handlerDetail.getResources().get("idpMetadata") != null) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(idpMetadata)) {
                    IOUtils.write(handlerDetail.getResources().get("idpMetadata").getBytes(),
                                    fileOutputStream);
                }

                fileResourceService
                                .uploadToDatabase("cas", "saml2", "idp-metadata", IDP_METADATA_PATH,
                                                handlerDetail.getResources().get("idpMetadata").getBytes());
            } else {
                Optional<FileResource> fileResource = fileResourceService.findByApplicationAndModuleAndKey("cas", "saml2", "idp-metadata");
                if (fileResource.isPresent()) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(idpMetadata)) {
                        IOUtils.write(fileResource.get().getFile(), fileOutputStream);
                    }
                }
            }

            if (handlerDetail.getResources().get("loginImage") != null) {
                fileResourceService.uploadToDatabase("cas", "saml2", "login-image", LOGIN_IMAGE_PATH,
                                handlerDetail.getResources().get("loginImage").getBytes());
            }
            try {
                generateSPMetadata(handlerDetail.getConfigData(), handlerDetail.getComplexUserAttributes(), spMetadata);
            } catch (Exception err) {
                log.error(err.getMessage(), err);
                throw new InvalidInputParamException(msgBundle.getText("invalid.user.source.config.code"),
                                msgBundle.getText("invalid.user.source.config.message"));
            }

            try (FileInputStream fileInputStream = new FileInputStream(spMetadata)) {
                fileResourceService.uploadToDatabase("cas", "saml2", "sp-metadata", SP_METADATA_PATH,
                                IOUtils.toByteArray(fileInputStream));
            }
        } catch(IOException e) {
            throw new ConsoleException(e.getMessage(), e);
        }

        secureStoreService.updateStoresToDatabase(SecureStoreFile.SAML2_KEY_STORE);
    }

    /**
     * Clean up uploaded resource files in database
     */
    @Override
    public void cleanUp()
            throws ConsoleException {
        secureStoreService.removeEntry(SecureStoreFile.SAML2_KEY_STORE.getStoreName(), "saml2");
        fileResourceService.removeByApplicationAndModule("cas", "saml2");

        try {
            Files.deleteIfExists(Paths.get(String.join(File.separator, controlCenterHome, "server", "configuration", "idp-metadata.xml")));
            Files.deleteIfExists(Paths.get(String.join(File.separator, controlCenterHome, "server", "configuration", "sp-metadata.xml")));
        } catch (IOException ioErr) {
            throw new ConsoleException(ioErr.getMessage(), ioErr);
        }
    }

    /**
     * Generate sp-metadata.xml file
     * @param configData Authentication handler configuration
     * @param userAttributeMapping List of user attribute mapping
     */
    private void generateSPMetadata(Map<String, String> configData, List<ComplexUserAttribute> userAttributeMapping, File spMetadata)
                    throws IOException, ParserConfigurationException, SAXException,
                    IllegalAccessException, ClassNotFoundException, InstantiationException {
        SAML2HttpClientBuilder httpClient = new SAML2HttpClientBuilder();
        httpClient.setConnectionTimeout(Duration.ofSeconds(5));
        httpClient.setSocketTimeout(Duration.ofSeconds(5));

        final SAML2Configuration configuration = new SAML2Configuration();
        configuration.setHttpClient(httpClient.build());
        configuration.setKeystorePath(String.join(File.separator,
                        controlCenterHome, "server", "certificates",
                        SecureStoreFile.SAML2_KEY_STORE.getStoreName().concat(".p12")));
        configuration.setKeystorePassword(keyStoreProperties.getPassword());
        configuration.setPrivateKeyPassword(keyStoreProperties.getPassword());
        configuration.setIdentityProviderMetadataPath(String.join(File.separator, controlCenterHome, "server", "configuration", "idp-metadata.xml"));
        configuration.setServiceProviderMetadataPath(String.join(File.separator, controlCenterHome, "server", "configuration", "sp-metadata.xml"));
        configuration.setKeystoreAlias(Saml2.DEFAULT_KEYSTORE_ALIAS);
        configuration.setSignMetadata(true);
        configuration.setForceKeystoreGeneration(true);
        configuration.setForceServiceProviderMetadataGeneration(true);
        configuration.setServiceProviderEntityId(configData.getOrDefault(AuthHandlerDetail.SP_ENTITY_ID, Saml2.DEFAULT_SP_ENTITY_ID));
        configuration.setCertificateNameToAppend(configData.getOrDefault(AuthHandlerDetail.CERTIFICATE_NAME_TO_APPEND, Saml2.DEFAULT_CERTIFICATION_NAME_TO_APPEND));
        configuration.setMaximumAuthenticationLifetime(Integer.parseInt(configData.getOrDefault(AuthHandlerDetail.MAX_AUTHENTICATION_LIFETIME, Saml2.DEFAULT_MAX_AUTHENTICATION_LIFETIME)));
        configuration.setAuthnRequestBindingType(configData.getOrDefault(AuthHandlerDetail.DESTINATION_BINDING, Saml2.DEFAULT_DESTINATION_BINDING));
        configuration.setAuthnContextClassRefs(Arrays.asList(configData.get(AuthHandlerDetail.AUTHENTICATION_CONTEXT_CLASS_REFERENCES.split(Saml2.MULTI_VALUE_DELIMITER))));
        configuration.setComparisonType(configData.get(AuthHandlerDetail.AUTHENTICATION_CONTEXT_COMPARISON_TYPE));
        configuration.setForceAuth(Boolean.valueOf(configData.get(AuthHandlerDetail.FORCE_AUTHENTICATION)));
        configuration.setPassive(Boolean.valueOf(configData.get(AuthHandlerDetail.PASSIVE)));
        configuration.setWantsAssertionsSigned(Boolean.valueOf(configData.get(AuthHandlerDetail.WANTS_ASSERTIONS_SIGNED)));
        configuration.setWantsResponsesSigned(Boolean.valueOf(configData.get(AuthHandlerDetail.WANTS_RESPONSE_SIGNED)));
        configuration.setNameIdPolicyFormat(configData.get(AuthHandlerDetail.NAME_ID_POLICY_FORMAT));
        configuration.setNameIdPolicyAllowCreate(Boolean.valueOf(configData.get(AuthHandlerDetail.NAME_ID_POLICY_ALLOW_CREATE)));
        configuration.setAllSignatureValidationDisabled(Boolean.valueOf(configData.get(AuthHandlerDetail.ALL_SIGNATURE_VALIDATION_DISABLED)));
        configuration.setUseNameQualifier(Boolean.valueOf(configData.get(AuthHandlerDetail.USE_NAME_QUALIFIER)));
        configuration.setAuthnRequestSigned(Boolean.valueOf(configData.get(AuthHandlerDetail.SIGN_AUTHENTICATION_REQUEST)));
        configuration.setSpLogoutRequestSigned(Boolean.valueOf(configData.get(AuthHandlerDetail.SIGN_SERVICE_PROVIDER_LOGOUT_REQUEST)));
        configuration.setSignatureAlgorithms(Arrays.asList(configData.get(AuthHandlerDetail.SIGNATURE_ALGORITHMS).split(Saml2.MULTI_VALUE_DELIMITER)));
        configuration.setSignatureReferenceDigestMethods(Arrays.asList(configData.get(AuthHandlerDetail.SIGNATURE_REFERENCE_DIGEST_METHODS).split(Saml2.MULTI_VALUE_DELIMITER)));
        configuration.setSignatureCanonicalizationAlgorithm(configData.get(AuthHandlerDetail.SIGNATURE_CANONICALIZATION_ALGORITHM));
        configuration.setAssertionConsumerServiceIndex(0);
        configuration.setAttributeConsumingServiceIndex(1);
        configuration.setProviderName(configData.getOrDefault(AuthHandlerDetail.PROVIDER_NAME, Saml2.DEFAULT_PROVIDER_NAME));

        for(ComplexUserAttribute userAttribute : userAttributeMapping) {
            configuration.getRequestedServiceProviderAttributes().add(
                new SAML2ServiceProvicerRequestedAttribute(userAttribute.getName(),
                                userAttribute.getFriendlyName(),
                                userAttribute.getNameFormat(),
                                userAttribute.isRequired()));
        }
        configuration.init();

        SAML2MetadataResolver metadataResolver = new SAML2ServiceProviderMetadataResolver(configuration);
        try(FileOutputStream fileOutputStream = new FileOutputStream(spMetadata)) {
            IOUtils.write(metadataResolver.getMetadata(), fileOutputStream, Charset.defaultCharset());
        }

        touchUpSPMetadata(spMetadata);
    }

    /**
     * CAS does not contain the service to return all the URL locations, need to manually set these attributes into respective
     * elements.
     * Print xml content in pretty print format
     *
     * @param spMetadata Service provider metadata file
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private void touchUpSPMetadata(File spMetadata)
                    throws IOException, ParserConfigurationException, SAXException,
                    InstantiationException, IllegalAccessException, ClassNotFoundException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setValidating(false);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(spMetadata);

        Element requestInitiator = (Element)document.getElementsByTagName("init:RequestInitiator").item(0);
        if(!requestInitiator.hasAttribute("Location")) {
            requestInitiator.setAttribute("Location", String.format(SERVICE_LOCATION, sanitizeServerName(serverName)));
        }
        Element assertionConsumerService = (Element)document.getElementsByTagName("md:AssertionConsumerService").item(0);
        if(!assertionConsumerService.hasAttribute("Location")) {
            assertionConsumerService.setAttribute("Location", String.format(SERVICE_LOCATION, sanitizeServerName(serverName)));
        }

        NodeList logoutServices = document.getElementsByTagName("md:SingleLogoutService");
        for(int i=0; i<logoutServices.getLength(); i++) {
            Element logoutService = (Element)logoutServices.item(i);
            if(!logoutService.hasAttribute("Location")) {
                logoutService.setAttribute("Location", String.format(SINGLE_LOGOUT_LOCATION, sanitizeServerName(serverName)));
            }
        }

        DOMImplementationLS domImplementation = (DOMImplementationLS) DOMImplementationRegistry.newInstance().getDOMImplementation("LS");
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);

        try(FileOutputStream fileOutputStream = new FileOutputStream(spMetadata)) {
            LSOutput lsOutput = domImplementation.createLSOutput();
            lsOutput.setEncoding(document.getXmlEncoding());
            lsOutput.setByteStream(fileOutputStream);

            lsSerializer.write(document, lsOutput);
        }
    }

    private String sanitizeServerName(String serverName) {
        if(serverName.startsWith("https:") && serverName.endsWith(":443")) {
            return serverName.substring(0, serverName.indexOf(":443"));
        }

        if(serverName.startsWith("http:") && serverName.endsWith(":80")) {
            return serverName.substring(0, serverName.indexOf(":80"));
        }

        return serverName;
    }
}
