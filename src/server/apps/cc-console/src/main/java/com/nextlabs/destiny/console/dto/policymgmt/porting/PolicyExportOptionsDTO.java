package com.nextlabs.destiny.console.dto.policymgmt.porting;

/**
 * 
 * @param sandeEnabled        Flag to indicate whether SANDE export format is enabled
 * @param plainTextEnabled    Flag to indicate whether plain text export format is enabled
 *
 */
public class PolicyExportOptionsDTO {
	
	private boolean sandeEnabled;
	private boolean plainTextEnabled;
	
	public boolean isSandeEnabled() {
		return sandeEnabled;
	}
	
	public void setSandeEnabled(boolean sandeEnabled) {
		this.sandeEnabled = sandeEnabled;
	}
	
	public boolean isPlainTextEnabled() {
		return plainTextEnabled;
	}
	
	public void setPlainTextEnabled(boolean plainTextEnabled) {
		this.plainTextEnabled = plainTextEnabled;
	}	

}
