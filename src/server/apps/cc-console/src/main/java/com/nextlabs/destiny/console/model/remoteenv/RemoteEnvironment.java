package com.nextlabs.destiny.console.model.remoteenv;

import com.nextlabs.destiny.console.model.BaseModel;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Mohammed Sainal Shah
 */
@Entity
@Table(name = "REMOTE_ENV")
public class RemoteEnvironment extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1246868125721291584L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "host")
    private String host;

    @Column(name = "port")
    private String port;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "is_active")
    private boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

}
