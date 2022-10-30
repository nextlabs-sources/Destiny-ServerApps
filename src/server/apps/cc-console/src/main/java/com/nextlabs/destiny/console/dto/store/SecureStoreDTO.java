package com.nextlabs.destiny.console.dto.store;

import com.nextlabs.destiny.console.dto.Auditable;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SecureStoreDTO
        implements Auditable {

    private String storeType;

    private String storeName;

    private String newPassword;

    private List<CertificateDTO> certificateDTO;

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public List<CertificateDTO> getCertificateDTO() {
        return certificateDTO;
    }

    public void setCertificateDTO(List<CertificateDTO> certificateDTO) {
        this.certificateDTO = certificateDTO;
    }

    @Override
    public String toAuditString()
            throws ConsoleException {
        try {
            Map<String, Object> audit = new LinkedHashMap<>();

            audit.put("Store Type", this.storeType);
            if(StringUtils.isNotEmpty(this.storeName)) {
                audit.put("Store Name", this.storeName);
            }

            if(certificateDTO != null
                && !certificateDTO.isEmpty()) {
                certificateDTO.sort(Comparator.comparing(CertificateDTO::getAlias));
                List<Map<String, Object>> certificates = new ArrayList<>();

                for(CertificateDTO cert : certificateDTO) {
                    Map<String, Object> certificate = new LinkedHashMap<>();
                    certificate.put("Alias", cert.getAlias());
                    certificate.put("Version", cert.getVersion());
                    certificate.put("Issuer", cert.getIssuer());
                    certificate.put("Subject DN", cert.getSubjectDN());
                    certificate.put("Serial Number", cert.getSerialNumber());
                    certificate.put("Type", cert.getType());
                    certificate.put("Key Algorithm", cert.getKeyAlgorithmName());
                    certificate.put("Key Size", cert.getKeySize());
                    if(cert.getValidFrom() > 0)
                        certificate.put("Valid From", DATE_FORMAT.format(new Date(cert.getValidFrom())));
                    if(cert.getValidUntil() > 0)
                        certificate.put("Valid Until", DATE_FORMAT.format(new Date(cert.getValidUntil())));

                    certificates.add(certificate);
                }

                audit.put("Entries", certificates);
            }

            return JsonUtil.toJsonString(audit);
        } catch(Exception e) {
            throw new ConsoleException(e);
        }
    }
}
