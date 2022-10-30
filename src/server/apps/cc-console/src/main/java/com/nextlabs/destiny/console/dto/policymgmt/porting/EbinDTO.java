package com.nextlabs.destiny.console.dto.policymgmt.porting;

/**
 * 
 * @param alias        Signature key alias
 * @param signature    Digital Signature
 * @param policyBundle Policy bundle
 *
 */
public class EbinDTO {

	private String alias;
	private String signature;
	private String policyBundle;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getPolicyBundle() {
		return policyBundle;
	}

	public void setPolicyBundle(String policyBundle) {
		this.policyBundle = policyBundle;
	}

}
