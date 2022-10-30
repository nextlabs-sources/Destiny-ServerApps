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
@Table(name = "COMPONENT")
public class Component implements Comparable<Component>, Serializable {

    public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_COMPONENT_URL = "componentUrl";
    public static final String ATTRIBUTE_TYPE = "type";
    public static final String ATTRIBUTE_LAST_HEART_BEAT= "lastHeartBeat";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "TYPEDISPLAYNAME")
    private String typeDisplayName;

    @Column(name = "CALLBACKURL")
    private String callBackUrl;

    @Column(name = "COMPONENTURL")
    private String componentUrl;

    @Column(name = "LASTHEARTBEAT")
    private long lastHeartBeat;

    @Column(name = "HEARTBEATRATE")
    private long heartBeatRate;

    @Column(name = "VERSION")
    private String version;

    public Component() {
    }

    public Component(Long id, String type, String componentUrl) {
        this.id = id;
        this.type = type;
        this.componentUrl = componentUrl;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeDisplayName() {
        return typeDisplayName;
    }

    public void setTypeDisplayName(String typeDisplayName) {
        this.typeDisplayName = typeDisplayName;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getComponentUrl() {
        return componentUrl;
    }

    public void setComponentUrl(String componentUrl) {
        this.componentUrl = componentUrl;
    }

    public long getLastHeartBeat() {
        return lastHeartBeat;
    }

    public void setLastHeartBeat(long lastHeartBeat) {
        this.lastHeartBeat = lastHeartBeat;
    }

    public long getHeartBeatRate() {
        return heartBeatRate;
    }

    public void setHeartBeatRate(long heartBeatRate) {
        this.heartBeatRate = heartBeatRate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(Component o) {
        if (o == null || this.id == null) {
            return -1;
        }
        return this.id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(id, component.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
