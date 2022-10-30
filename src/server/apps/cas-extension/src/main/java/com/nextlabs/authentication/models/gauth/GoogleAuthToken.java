package com.nextlabs.authentication.models.gauth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.apereo.cas.authentication.OneTimeToken;

/**
 * Entity for Google Authenticator token.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "MFA_GOOGLE_AUTH_TOKEN")
public class GoogleAuthToken implements Serializable {

    private static final long serialVersionUID = -7565487521014838765L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true, nullable = false)
    private long id;

    @Column(name = "TOKEN", nullable = false)
    private Integer token;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "ISSUED_DATE_TIME", nullable = false)
    private LocalDateTime issuedDateTime = LocalDateTime.now();

    public GoogleAuthToken() {
    }

    public GoogleAuthToken(OneTimeToken token) {
        this.id = token.getId();
        this.token = token.getToken();
        this.username = token.getUserId() == null ? null : token.getUserId().toLowerCase();
        this.issuedDateTime = token.getIssuedDateTime();
    }

    public OneTimeToken getOneTimeToken() {
        OneTimeToken oneTimeToken = new OneTimeToken(this.token, this.username);
        oneTimeToken.setId(this.id);
        oneTimeToken.setIssuedDateTime(this.issuedDateTime);
        return oneTimeToken;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getToken() {
        return token;
    }

    public void setToken(Integer token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getIssuedDateTime() {
        return issuedDateTime;
    }

    public void setIssuedDateTime(LocalDateTime issuedDateTime) {
        this.issuedDateTime = issuedDateTime;
    }

}