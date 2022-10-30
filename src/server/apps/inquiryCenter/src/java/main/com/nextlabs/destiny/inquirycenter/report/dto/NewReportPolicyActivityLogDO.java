/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.report.dto;

import java.util.HashMap;
import java.util.Map;

import com.bluejungle.domain.policydecision.PolicyDecisionEnumType;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.ReportBaseLogDO;

/**
 * @author nnallagatla
 *
 */
public class NewReportPolicyActivityLogDO extends ReportBaseLogDO{
    private Long decisionRequestId;
    private PolicyDecisionEnumType policyDecision;
    private Long policyId;
    
    private Map<String, Map<String, String>> extendedAttributes;
    
    public NewReportPolicyActivityLogDO() {
    	extendedAttributes = new HashMap<String, Map<String,String>>();
	}
    
    /**
     * These are the new fields added in the mirror table that will be used
     * for reporting. These are here to reduce/remove the need for table joins
     * during queries.
     */
    
    /**
     * From cached_policy.name
     */
    private String policyName; 
    
    /**
     * From cached_policy.fullname
     */
    private String policyFullName;
    
    public Long getDecisionRequestId() {
        return decisionRequestId;
    }
    
    public void setDecisionRequestId(Long decisionRequestId) {
        this.decisionRequestId = decisionRequestId;
    }
    
    public PolicyDecisionEnumType getPolicyDecision() {
        return policyDecision;
    }
    
    public void setPolicyDecision(PolicyDecisionEnumType policyDecision) {
        this.policyDecision = policyDecision;
    }
    
    public Long getPolicyId() {
        return policyId;
    }
    
    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
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

    public void setPolicyFullName(String policyFullName) {
        this.policyFullName = policyFullName;
    }
    
    /**
     * 
     * @param attributeName
     * @param attributeType {USER, RESOURCE}
     * @return
     */
    public String getAttribute(String attributeName, String attributeType)
    {
    	if (attributeName == null || attributeName.isEmpty() || attributeType == null || attributeType.isEmpty())
    	{
    		return null;
    	}
    	
    	Map<String, String> attrTypeMap = extendedAttributes.get(attributeType);
    	
    	if (attrTypeMap == null)
    	{
    		return null;
    	}
    	
    	return attrTypeMap.get(attributeName);
    }
    
    /**
     * 
     * @param attributeName
     * @param attributeType
     * @param attributeValue
     */
    public void setAttribute(String attributeName, String attributeType, String attributeValue)
    {
    	if (attributeName == null || attributeName.isEmpty() || attributeType == null || attributeType.isEmpty())
    	{
    		return;
    	}
    	
    	Map<String, String> attrTypeMap = extendedAttributes.get(attributeType);
    	
    	if (attrTypeMap == null)
    	{
    		attrTypeMap = new HashMap<String, String>();
    		extendedAttributes.put(attributeType, attrTypeMap);
    	}
    	attrTypeMap.put(attributeName, attributeValue);
    }
}
