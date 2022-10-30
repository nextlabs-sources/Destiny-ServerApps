package com.nextlabs.destiny.console.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "AGENT")
public class Agent implements Comparable<Agent>, Serializable {
    public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_HOST = "host";
    public static final String ATTRIBUTE_TYPE = "type";
    public static final String ATTRIBUTE_REGISTERED = "registered";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "HOST", length = 128)
    private String host;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "REGISTERED")
    private boolean registered;

    @Column(name = "PUSHPORT")
    private int pushPort;

    @Column(name = "ISPUSHREADY")
    private boolean isPushReady;

    @Column(name = "LASTHEARTBEAT")
    private long lastHeartBeat;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "DEPLOYMENT_BUNDLE_TS")
    private long deploymentBundleTs;

    @Column(name = "AGENT_PROFILE_TS")
    private long agentProfileTs;

    @Column(name = "AGENT_PROFILE_NAME")
    private String agentProfileName;

    @Column(name = "COMM_PROFILE_TS")
    private long commProfileTs;

    @Column(name = "COMM_PROFILE_NAME")
    private String commProfileName;

    @Column(name = "COMM_PROFILE_ID")
    private long commProfileId;

    @Column(name = "AGENT_PROFILE_ID")
    private long agentProfileId;


    public Agent() {
    }

    public Agent(Long id, String type) {
        this.id = id;
        this.type = type;
    }

    public Agent(Long id, String host, String type) {
        this.id = id;
        this.host = host;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public int getPushPort() {
        return pushPort;
    }

    public void setPushPort(int pushPort) {
        this.pushPort = pushPort;
    }

    public boolean isPushReady() {
        return isPushReady;
    }

    public void setPushReady(boolean pushReady) {
        isPushReady = pushReady;
    }

    public long getLastHeartBeat() {
        return lastHeartBeat;
    }

    public void setLastHeartBeat(long lastHeartBeat) {
        this.lastHeartBeat = lastHeartBeat;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getDeploymentBundleTs() {
        return deploymentBundleTs;
    }

    public void setDeploymentBundleTs(long deploymentBundleTs) {
        this.deploymentBundleTs = deploymentBundleTs;
    }

    public long getAgentProfileTs() {
        return agentProfileTs;
    }

    public void setAgentProfileTs(long agentProfileTs) {
        this.agentProfileTs = agentProfileTs;
    }

    public String getAgentProfileName() {
        return agentProfileName;
    }

    public void setAgentProfileName(String agentProfileName) {
        this.agentProfileName = agentProfileName;
    }

    public long getCommProfileTs() {
        return commProfileTs;
    }

    public void setCommProfileTs(long commProfileTs) {
        this.commProfileTs = commProfileTs;
    }

    public String getCommProfileName() {
        return commProfileName;
    }

    public void setCommProfileName(String commProfileName) {
        this.commProfileName = commProfileName;
    }

    public long getCommProfileId() {
        return commProfileId;
    }

    public void setCommProfileId(long commProfileId) {
        this.commProfileId = commProfileId;
    }

    public long getAgentProfileId() {
        return agentProfileId;
    }

    public void setAgentProfileId(long agentProfileId) {
        this.agentProfileId = agentProfileId;
    }

    @Override
    public int compareTo(Agent o) {
        if (o == null || this.id == null) {
            return -1;
        }
        return this.id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agent agent = (Agent) o;
        return Objects.equals(id, agent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
