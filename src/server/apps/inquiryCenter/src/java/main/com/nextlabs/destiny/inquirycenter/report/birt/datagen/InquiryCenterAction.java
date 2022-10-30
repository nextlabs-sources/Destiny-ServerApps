/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 13 Jan 2017
 *
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

/**
 * Object representing an Actions contains actions display name, origin
 * (file/database), and last updated time stamp
 *
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public class InquiryCenterAction {

	private Long policyModelId = -1L;
	private String longName;
	private String origin;
	private long lastUpdated;

	public static final String ORIGIN_FILE = "FILE";
	public static final String ORIGIN_DB = "DB";

	public InquiryCenterAction() {

	}

	public InquiryCenterAction(String longName) {
		this.longName = longName;
		this.lastUpdated = System.currentTimeMillis();
		this.origin = ORIGIN_FILE;
	}

	public InquiryCenterAction(String longName, String origin) {
		this.longName = longName;
		this.origin = origin;
		this.lastUpdated = System.currentTimeMillis();
	}

	public InquiryCenterAction(String longName, String origin, Long policyModelId) {
		this.policyModelId = policyModelId;
		this.longName = longName;
		this.origin = origin;
		this.lastUpdated = System.currentTimeMillis();
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Long getPolicyModelId() {
		return policyModelId;
	}

	public void setPolicyModelId(Long policyModelId) {
		this.policyModelId = policyModelId;
	}

}
