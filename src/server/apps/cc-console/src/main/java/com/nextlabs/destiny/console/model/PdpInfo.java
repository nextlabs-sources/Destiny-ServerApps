package com.nextlabs.destiny.console.model;
public class PdpInfo {
	private int id;
	private String host;
	private String memory;
	private int vcpu;

	public PdpInfo() {
		super();
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the memory
	 */
	public String getMemory() {
		return memory;
	}
	/**
	 * @param memory
	 *            the memory to set
	 */
	public void setMemory(String memory) {
		this.memory = memory;
	}
	/**
	 * @return the vcpu
	 */
	public int getVcpu() {
		return vcpu;
	}
	/**
	 * @param vcpu
	 *            the vcpu to set
	 */
	public void setVcpu(int vcpu) {
		this.vcpu = vcpu;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PdpInfo [id=").append(id).append(", host=").append(host)
				.append(", memory=").append(memory).append(", vcpu=")
				.append(vcpu).append("]");
		return builder.toString();
	}
}