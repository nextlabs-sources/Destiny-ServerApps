package com.nextlabs.destiny.console.dto.store;

import com.nextlabs.destiny.console.dto.Auditable;
import com.nextlabs.destiny.console.enums.EntryType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.utils.JsonUtil;

import java.math.BigInteger;
import java.util.*;

public class CertificateDTO
        implements Auditable {

    private String storeName;

    private String alias;

    private int version;

    private String issuer;

    private String subjectDN;

    private BigInteger serialNumber;

    private String type;

    private String keyAlgorithmName;

    private int keySize;

    private String encodedKey;

    private String signatureAlgorithmName;

    private Map<String, String> namedExtensions;

    private Map<String, String> thumbprints;

    private int validity;

    private long validFrom;

    private long validUntil;

    private boolean isEffective;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSubjectDN() {
        return subjectDN;
    }

    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyAlgorithmName() {
        return keyAlgorithmName;
    }

    public void setKeyAlgorithmName(String keyAlgorithmName) {
        this.keyAlgorithmName = keyAlgorithmName;
    }

    public int getKeySize() {
        return keySize;
    }

    public String getEncodedKey() {
        return encodedKey;
    }

    public void setEncodedKey(String encodedKey) {
        this.encodedKey = encodedKey;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public String getSignatureAlgorithmName() {
        return signatureAlgorithmName;
    }

    public void setSignatureAlgorithmName(String signatureAlgorithmName) {
        this.signatureAlgorithmName = signatureAlgorithmName;
    }

    public Map<String, String> getNamedExtensions() {
        if(namedExtensions == null) {
           namedExtensions = new LinkedHashMap<>();
        }
        return namedExtensions;
    }

    public void setNamedExtensions(Map<String, String> namedExtensions) {
        this.namedExtensions = namedExtensions;
    }

    public Map<String, String> getThumbprints() {
        return thumbprints;
    }

    public void setThumbprints(Map<String, String> thumbprints) {
        this.thumbprints = thumbprints;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(long validFrom) {
        this.validFrom = validFrom;
    }

    public long getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(long validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isEffective() {
        if(EntryType.SECRET_KEY.name().equalsIgnoreCase(this.type)) {
            isEffective = true;
        } else {
            long now = System.currentTimeMillis();
            isEffective = (now >= this.validFrom) && (now <= this.validUntil);
        }

        return this.isEffective;
    }

    @Override
    public String toAuditString()
            throws ConsoleException {
        try {
            Map<String, Object> audit = new LinkedHashMap<>();

            audit.put("Store Name", this.storeName);
            audit.put("Key Type", this.type);
            audit.put("Alias", this.alias);
            audit.put("Version", this.version);
            audit.put("Issuer", this.issuer);
            audit.put("Subject DN", this.subjectDN);
            audit.put("Serial Number", this.serialNumber);
            audit.put("Type", this.type);
            audit.put("Key Algorithm", this.keyAlgorithmName);
            audit.put("Key Size", this.keySize);
            audit.put("Signature Algorithm", this.signatureAlgorithmName);
            if(this.validFrom > 0)
                audit.put("Valid From", DATE_FORMAT.format(new Date(this.validFrom)));

            if(this.validUntil > 0)
                audit.put("Valid Until", DATE_FORMAT.format(new Date(this.validUntil)));

            return JsonUtil.toJsonString(audit);
        } catch(Exception e) {
            throw new ConsoleException(e);
        }
    }
}
