package com.nextlabs.destiny.console.enums;

public enum SecureStoreFile {

    AGENT_KEY_STORE(SecureStoreType.KEYSTORE, SecureStoreType.KEYSTORE, SecureStoreFormat.PKCS12, "agent-keystore", true),
    AGENT_TRUST_STORE(SecureStoreType.TRUSTSTORE, SecureStoreType.TRUSTSTORE, SecureStoreFormat.PKCS12, "agent-truststore", true),
    APPLICATION_KEY_STORE(SecureStoreType.KEYSTORE, SecureStoreType.KEYSTORE, SecureStoreFormat.PKCS12, "application-keystore", true),
    APPLICATION_TRUST_STORE(SecureStoreType.TRUSTSTORE, SecureStoreType.TRUSTSTORE, SecureStoreFormat.PKCS12, "application-truststore", true),
    CACERTS_TRUST_STORE(SecureStoreType.TRUSTSTORE, SecureStoreType.TRUSTSTORE, SecureStoreFormat.JKS, "cacerts", true),
    DCC_KEY_STORE(SecureStoreType.KEYSTORE, SecureStoreType.KEYSTORE, SecureStoreFormat.PKCS12, "dcc-keystore", true),
    DCC_TRUST_STORE(SecureStoreType.TRUSTSTORE, SecureStoreType.TRUSTSTORE, SecureStoreFormat.PKCS12, "dcc-truststore", true),
    DIGITAL_SIGNATURE_KEY_STORE(SecureStoreType.KEYSTORE, SecureStoreType.KEYSTORE, SecureStoreFormat.PKCS12, "digital-signature-keystore", true),
    DIGITAL_SIGNATURE_TRUST_STORE(SecureStoreType.TRUSTSTORE, SecureStoreType.TRUSTSTORE, SecureStoreFormat.PKCS12, "digital-signature-truststore", true),
    FPE_KEY_STORE(SecureStoreType.KEYSTORE, SecureStoreType.KEYSTORE, SecureStoreFormat.PKCS12, "fpe-keystore", false),
    LEGACY_AGENT_KEY_STORE(SecureStoreType.KEYSTORE, SecureStoreType.KEYSTORE, SecureStoreFormat.PKCS12, "legacy-agent-keystore", true),
    LEGACY_AGENT_TRUST_STORE(SecureStoreType.TRUSTSTORE, SecureStoreType.KEYSTORE, SecureStoreFormat.PKCS12, "legacy-agent-truststore", true),
    LEGACY_AGENT_TRUST_STORE_KP(SecureStoreType.TRUSTSTORE, SecureStoreType.KEYSTORE, SecureStoreFormat.PKCS12, "legacy-agent-truststore-kp", true),
    SAML2_KEY_STORE(SecureStoreType.KEYSTORE, SecureStoreType.KEYSTORE, SecureStoreFormat.PKCS12, "saml2-keystore", false),
    WEB_KEY_STORE(SecureStoreType.KEYSTORE, SecureStoreType.KEYSTORE, SecureStoreFormat.PKCS12, "web-keystore", true),
    WEB_TRUST_STORE(SecureStoreType.TRUSTSTORE, SecureStoreType.TRUSTSTORE, SecureStoreFormat.PKCS12, "web-truststore", true);
    private SecureStoreFormat storeFormat;
    private SecureStoreType storeType;

    private SecureStoreType passwordType;

    private String storeName;

    private boolean uiManageable;

    SecureStoreFile(SecureStoreType storeType, SecureStoreType passwordType, SecureStoreFormat storeFormat, String storeName, boolean uiManageable) {
        this.storeType = storeType;
        this.passwordType = passwordType;
        this.storeFormat = storeFormat;
        this.storeName = storeName;
        this.uiManageable = uiManageable;
    }

    public SecureStoreType getStoreType() {
        return this.storeType;
    }

    public SecureStoreType getPasswordType() {
        return this.passwordType;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public boolean getUIManageable() {
        return this.uiManageable;
    }

    public String getStoreFormat() {
        return this.storeFormat.name();
    }

    public void setStoreFormat(SecureStoreFormat storeFormat) {
        this.storeFormat = storeFormat;
    }

    public static SecureStoreFile getStoreFileByName(String storeName) {
        for (SecureStoreFile storeFile : values()) {
            if (storeFile.getStoreName().equalsIgnoreCase(storeName)) {
                return storeFile;
            }
        }

        return null;
    }
}
