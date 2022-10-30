 /*
 *  All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;


public class PADetailsTableData  extends DetailsTableData {
    
    private long policyId;
    private String policyDecision;
    private long decisionRequestId;
    private String policyFullName;
    private String policyName;

    public long getPolicyId() {
        return policyId;
    }
    
    public void setPolicyId(long policyId) {
        this.policyId = policyId;
    }
    
    public String getPolicyDecision() {
        return policyDecision;
    }
    
    public void setPolicyDecision(String policyDecision) {
        // TODO: these strings should not be hardcoded here
        this.policyDecision = "A".equals(policyDecision) ? "Allow" : "Deny";;
    }
    
    public long getDecisionRequestId() {
        return decisionRequestId;
    }
    
    public void setDecisionRequestId(long decisionRequestId) {
        this.decisionRequestId = decisionRequestId;
    }
    
    public void setPolicyFullName(String policyFullName) {
        if ( policyFullName == null) {
            policyFullName = "Unknown Policy";
        }
        /*} else if (policyFullName.lastIndexOf("/", policyFullName.length()) > 0) {
            if (policyFullName.substring(policyFullName.lastIndexOf(
                "/", policyFullName.length())+1, policyFullName.length()).length() > 40) {
                    policyFullName.substring(policyFullName.lastIndexOf("/", policyFullName.length())+1, policyFullName.length()).substring(0, 37) + " ... " 
            } else {
                policyFullName.substring(policyFullName.lastIndexOf("/", policyFullName.length())+1, policyFullName.length())
            }
        } else {
            if (policyFullName.length() > 40) {
                    policyFullName.substring(0, 37) + " ... "
            } else {
                    policyFullName   
            }
    }*/
        this.policyFullName = policyFullName;
    }

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getPolicyFullName() {
		return policyFullName;
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PADetailsTableData [policyId=");
		builder.append(policyId);
		builder.append(", policyDecision=");
		builder.append(policyDecision);
		builder.append(", decisionRequestId=");
		builder.append(decisionRequestId);
		builder.append(", policyFullName=");
		builder.append(policyFullName);
		builder.append(", policyName=");
		builder.append(policyName);
		builder.append("]");
		return builder.toString();
	}
    
}
