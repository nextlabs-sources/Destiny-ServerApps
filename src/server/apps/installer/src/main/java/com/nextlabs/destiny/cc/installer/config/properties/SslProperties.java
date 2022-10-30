package com.nextlabs.destiny.cc.installer.config.properties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.nextlabs.destiny.cc.installer.annotations.ValidCaCertsFolder;
import com.nextlabs.destiny.cc.installer.annotations.ValidCertificate;
import com.nextlabs.destiny.cc.installer.config.properties.validationgroups.CaCertValidation;

/**
 * SSL properties.
 *
 * @author Sachindra Dasun
 */
@ValidCaCertsFolder(message = "{ssl.validCaCertsFolder}", groups = CaCertValidation.class)
public class SslProperties {

    @NotNull(message = "{ssl.caCerts.notNull}")
    private List<@ValidCertificate(message = "{ssl.validCertificate}", groups = CaCertValidation.class)
            CertificateProperties> caCerts = new ArrayList<>();
    @NestedConfigurationProperty
    @NotNull(message = "{ssl.cc.notNull}")
    private KeyPairProperties ccKeyPairStore = new KeyPairProperties();
    @NestedConfigurationProperty
    @NotNull(message = "{ssl.keystore.notNull}")
    private CertificateStoreProperties installerKeystore = new CertificateStoreProperties();
    @NestedConfigurationProperty
    @NotNull(message = "{ssl.truststore.notNull}")
    private CertificateStoreProperties installerTruststore = new CertificateStoreProperties();
    @NestedConfigurationProperty
    @NotNull(message = "{ssl.keystore.notNull}")
    @Valid
    private CertificateStoreProperties keystore = new CertificateStoreProperties();
    @NestedConfigurationProperty
    @NotNull(message = "{ssl.truststore.notNull}")
    @Valid
    private CertificateStoreProperties truststore = new CertificateStoreProperties();
    @NestedConfigurationProperty
    @NotNull(message = "{ssl.web.notNull}")
    private KeyPairProperties webKeyPairStore = new KeyPairProperties();
    private String webCertCn;

    public SslProperties() {
        this.getInstallerKeystore().setPassword(RandomStringUtils.random(32, true, true));
        this.getInstallerTruststore().setPassword(RandomStringUtils.random(32, true, true));
    }

    public CertificateStoreProperties getInstallerKeystore() {
        return installerKeystore;
    }

    public void setInstallerKeystore(CertificateStoreProperties installerKeystore) {
        this.installerKeystore = installerKeystore;
    }

    public CertificateStoreProperties getInstallerTruststore() {
        return installerTruststore;
    }

    public void setInstallerTruststore(CertificateStoreProperties installerTruststore) {
        this.installerTruststore = installerTruststore;
    }

    public List<CertificateProperties> getCaCerts() {
        return caCerts;
    }

    public void setCaCerts(List<CertificateProperties> caCerts) {
        this.caCerts = caCerts;
    }

    public CertificateStoreProperties getTruststore() {
        return truststore;
    }

    public void setTruststore(CertificateStoreProperties truststore) {
        this.truststore = truststore;
    }

    public CertificateStoreProperties getKeystore() {
        return keystore;
    }

    public void setKeystore(CertificateStoreProperties keystore) {
        this.keystore = keystore;
    }

    public KeyPairProperties getCcKeyPairStore() {
        return ccKeyPairStore;
    }

    public void setCcKeyPairStore(KeyPairProperties ccKeyPairStore) {
        this.ccKeyPairStore = ccKeyPairStore;
    }

    public KeyPairProperties getWebKeyPairStore() {
        return webKeyPairStore;
    }

    public void setWebKeyPairStore(KeyPairProperties webKeyPairStore) {
        this.webKeyPairStore = webKeyPairStore;
    }

    public String getWebCertCn() {
        return webCertCn;
    }

    public void setWebCertCn(String webCertCn) {
        this.webCertCn = webCertCn;
    }

}
