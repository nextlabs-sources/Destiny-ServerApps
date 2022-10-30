/*
 * Copyright 2016by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 19 Sep 2016
 *
 */
package com.nextlabs.destiny.console.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 *
 *
 * @author kyu
 * @since 8.0
 *
 */
public final class SysInfo {
	@JsonProperty("license_info")
	private LicenseInfo licenseInfo; // license_info
	@JsonProperty("pdp_info")
	private PdpInfo[] pdpInfo; // pdp_info

	/**
	 * @return the licenseInfo
	 */
	public LicenseInfo getLicenseInfo() {
		return licenseInfo;
	}
	/**
	 * @param licenseInfo
	 *            the licenseInfo to set
	 */
	public void setLicenseInfo(LicenseInfo licenseInfo) {
		this.licenseInfo = licenseInfo;
	}
	/**
	 * @return the pdpInfo
	 */
	public PdpInfo[] getPdpInfo() {
		return pdpInfo;
	}
	/**
	 * @param pdpInfo
	 *            the pdpInfo to set
	 */
	public void setPdpInfo(PdpInfo[] pdpInfo) {
		this.pdpInfo = pdpInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SysInfo [licenseInfo=").append(licenseInfo)
				.append(", pdpInfo=").append(Arrays.toString(pdpInfo))
				.append("]");
		return builder.toString();
	}

	public class LicenseInfo {
		@JsonProperty("expiry_date")
		private String expiryDate; // expiry_date
		@JsonProperty("subcription_mode")
		private String subcriptionMode; // subcription_mode
		
		public LicenseInfo() {
			super();
		}
		/**
		 * @return the expiryDate
		 */
		public String getExpiryDate() {
			return expiryDate;
		}
		/**
		 * @param expiryDate
		 *            the expiryDate to set
		 */
		public void setExpiryDate(String expiryDate) {
			this.expiryDate = expiryDate;
		}
		/**
		 * @return the subcriptionMode
		 */
		public String getSubcriptionMode() {
			return subcriptionMode;
		}
		/**
		 * @param subcriptionMode
		 *            the subcriptionMode to set
		 */
		public void setSubcriptionMode(String subcriptionMode) {
			this.subcriptionMode = subcriptionMode;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("LicenseInfo [expiryDate=").append(expiryDate)
					.append(", subcriptionMode=").append(subcriptionMode)
					.append("]");
			return builder.toString();
		}

	}
	
}
