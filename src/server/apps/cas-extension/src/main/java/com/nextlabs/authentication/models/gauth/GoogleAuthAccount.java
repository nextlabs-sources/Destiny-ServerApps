package com.nextlabs.authentication.models.gauth;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apereo.cas.authentication.OneTimeTokenAccount;
import org.apereo.cas.gauth.credential.GoogleAuthenticatorAccount;

/**
 * Entity for Google Authenticator account.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "MFA_GOOGLE_AUTH_ACCOUNT")
public class GoogleAuthAccount implements Serializable {

    private static final long serialVersionUID = 1076466835817896507L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "SECRET_KEY", length = 2048, nullable = false)
    private String secretKey;

    @Column(name = "VALIDATION_CODE", nullable = false)
    private Integer validationCode;

    @Column(name = "ACCOUNT_NAME", nullable = false)
    private String accountName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "MFA_GOOGLE_AUTH_CODE", joinColumns = @JoinColumn(name = "ACCOUNT_ID"))
    @Column(name = "SCRATCH_CODE", nullable = false)
    private List<Integer> scratchCodes = new ArrayList<>();

    @Column(name = "USERNAME", unique = true, nullable = false)
    private String username;

    @Column(name = "REGISTRATION_DATE")
    private ZonedDateTime registrationDate = ZonedDateTime.now(ZoneOffset.UTC);

    public GoogleAuthAccount() {
    }

    public GoogleAuthAccount(OneTimeTokenAccount account) {
        this.id = account.getId();
        this.secretKey = account.getSecretKey();
        this.validationCode = account.getValidationCode();
        this.accountName = account.getName() == null ? "no_name" : account.getName();
        this.scratchCodes = account.getScratchCodes();
        this.username = account.getUsername() == null ? null : account.getUsername().toLowerCase();
        this.registrationDate = account.getRegistrationDate();
    }

    public OneTimeTokenAccount getOneTimeTokenAccount() {
        OneTimeTokenAccount account = new OneTimeTokenAccount();
        account.setId(this.id);
        account.setUsername(this.username);
        account.setSecretKey(this.secretKey);
        account.setValidationCode(this.validationCode);
        account.setName(this.accountName);
        account.setScratchCodes(this.scratchCodes);
        account.setRegistrationDate(this.registrationDate);
        return account;
    }

    public GoogleAuthenticatorAccount getGoogleAuthenticatorAccount() {
        GoogleAuthenticatorAccount account = new GoogleAuthenticatorAccount();
        account.setId(this.id);
        account.setUsername(this.username);
        account.setSecretKey(this.secretKey);
        account.setValidationCode(this.validationCode);
        account.setName(this.accountName);
        account.setScratchCodes(this.scratchCodes);
        account.setRegistrationDate(this.registrationDate);
        return account;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(int validationCode) {
        this.validationCode = validationCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public List<Integer> getScratchCodes() {
        return scratchCodes;
    }

    public void setScratchCodes(List<Integer> scratchCodes) {
        this.scratchCodes = scratchCodes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ZonedDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(ZonedDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

}
