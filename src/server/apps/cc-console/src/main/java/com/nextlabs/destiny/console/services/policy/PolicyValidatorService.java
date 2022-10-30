/**
 * 
 */
package com.nextlabs.destiny.console.services.policy;

import java.util.List;

import org.apache.openaz.xacml.api.pdp.PDPException;

import com.nextlabs.destiny.console.dto.policymgmt.policyvalidator.GenericEvaluationLog;
import com.nextlabs.destiny.console.dto.policymgmt.policyvalidator.PolicyEvaluationResult;
import com.nextlabs.destiny.console.dto.policymgmt.policyvalidator.PolicyValidationDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
public interface PolicyValidatorService {
	
	
    PolicyEvaluationResult policyValidator(PolicyValidationDTO policyValidationDTO) throws ConsoleException, PDPException;
    
    List<GenericEvaluationLog> getValidationLogs(String logId) throws ConsoleException;
}
