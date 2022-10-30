package com.nextlabs.authentication.models;

public class PdpInfo {

    private int id;
    private String host;
    private String memory;
    private int vcpu;

    public PdpInfo() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public int getVcpu() {
        return vcpu;
    }

    public void setVcpu(int vcpu) {
        this.vcpu = vcpu;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PdpInfo [id=").append(id).append(", host=").append(host)
                .append(", memory=").append(memory).append(", vcpu=")
                .append(vcpu).append("]");
        return builder.toString();
    }

}