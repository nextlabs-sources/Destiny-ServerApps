/**
 * 
 */
package com.nextlabs.destiny.console.services.policy.impl;

import static com.nextlabs.destiny.console.enums.PolicyDevelopmentStatus.DELETED;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.openaz.pepapi.Action;
import org.apache.openaz.pepapi.Environment;
import org.apache.openaz.pepapi.PepAgent;
import org.apache.openaz.pepapi.PepResponse;
import org.apache.openaz.pepapi.Resource;
import org.apache.openaz.pepapi.Subject;
import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.api.XACML1;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.api.pdp.PDPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bluejungle.pf.destiny.formatter.DomainObjectFormatter;
import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.domain.destiny.misc.EffectType;
import com.bluejungle.pf.domain.destiny.obligation.LogObligation;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.epicenter.exceptions.IPolicyExceptions;
import com.bluejungle.pf.domain.epicenter.exceptions.IPolicyReference;
import com.bluejungle.pf.domain.epicenter.misc.IObligation;
import com.nextlabs.destiny.console.dao.policy.PolicyDevelopmentEntityDao;
import com.nextlabs.destiny.console.delegadmin.helpers.DelegationRuleReferenceResolver;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.policyvalidator.Attribute;
import com.nextlabs.destiny.console.dto.policymgmt.policyvalidator.GenericEvaluationLog;
import com.nextlabs.destiny.console.dto.policymgmt.policyvalidator.Obligation;
import com.nextlabs.destiny.console.dto.policymgmt.policyvalidator.ObligationParameter;
import com.nextlabs.destiny.console.dto.policymgmt.policyvalidator.PolicyEvaluationResult;
import com.nextlabs.destiny.console.dto.policymgmt.policyvalidator.PolicyValidationDTO;
import com.nextlabs.destiny.console.dto.policymgmt.policyvalidator.Status;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicySearchService;
import com.nextlabs.destiny.console.services.policy.PolicyValidatorService;
import com.nextlabs.destiny.console.utils.PolicyValidationIdGenerator;
import com.nextlabs.destiny.console.utils.logfilehandler.FileHandlerForPolicyEvaluation;
import com.nextlabs.openaz.pepapi.Application;
import com.nextlabs.openaz.pepapi.DiscretionaryPolicies;
import com.nextlabs.openaz.pepapi.Host;
import com.nextlabs.openaz.pepapi.Recipient;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
@Service
public class PolicyValidatorServiceImpl implements PolicyValidatorService {

	private static final Logger log = LoggerFactory.getLogger(PolicyValidatorServiceImpl.class);

	private static final String PQL_SEPARATOR = "";
	private static final List<String> DEPLOYED_ONLY_STATUS = new ArrayList<>();
	private static final String LOG_DELIMITER = "_#_";

	static {
		DEPLOYED_ONLY_STATUS.add("APPROVED");
	}

	@Autowired
	private PepAgent pepAgent;

	@Autowired
	protected MessageBundleService msgBundle;

	@Autowired
	private PolicySearchService policySearchService;

	@Autowired
	private PolicyDevelopmentEntityDao policyDevelopmentEntityDao;

	@Autowired
	private PolicyDevelopmentEntityMgmtService devEntityMgmtService;

	@Override
	public PolicyEvaluationResult policyValidator(PolicyValidationDTO policyValidationDTO) throws ConsoleException, PDPException {
		log.debug("Request came to validate policies, [ Data: {}]", policyValidationDTO);
		Long logId = PolicyValidationIdGenerator.generateId();
		Subject subject = createSubjectwithAttributes(policyValidationDTO);
		Action action = createActions(policyValidationDTO);
		Resource resource = createResourcewithAttributes(policyValidationDTO);
		Environment environment = createEnvironmentwithAttributes(policyValidationDTO, logId);
		Application app = createApplicationwithAttributes(policyValidationDTO);
		Host host = createHostwithAttributes(policyValidationDTO);
		Recipient recipient = createRecipientwithAttributes(policyValidationDTO);

		List<Object> parameters = new ArrayList<>();
		parameters.add(subject);
		parameters.add(action);
		parameters.add(resource);
		parameters.add(environment);
		parameters.add(app);
		parameters.add(host);
		if (recipient != null) {
			parameters.add(recipient);
		}

		if (policyValidationDTO.getOnDemandPolicyIds() != null
				&& !policyValidationDTO.getOnDemandPolicyIds().isEmpty()) {
			// generate pql from specified policies
			List<String> pqlFromPolicyIds = findPqlFromPolicyIds(policyValidationDTO.getOnDemandPolicyIds(), true,
					false);
			DiscretionaryPolicies pql = DiscretionaryPolicies
					.newInstance(StringUtils.join(pqlFromPolicyIds, PQL_SEPARATOR), true);
			parameters.add(pql);
		} else if (policyValidationDTO.getOnDemandPolicyCriteria() != null
				&& !policyValidationDTO.getOnDemandPolicyCriteria().isEmpty()) {
			// search policies and generate pql;
			List<String> pqlFromPolicyIds = getPqlFromPolicySearchCriteria(
					policyValidationDTO.getOnDemandPolicyCriteria());
			DiscretionaryPolicies pql = DiscretionaryPolicies
					.newInstance(StringUtils.join(pqlFromPolicyIds, PQL_SEPARATOR), true);
			parameters.add(pql);
		}

		PolicyEvaluationResult result = validateRequest(logId, parameters);
		return result;
	}

	private PolicyEvaluationResult validateRequest(Long logId, List<Object> parameters) throws PDPException {
		log.debug("Before validate request, [ Internal log id: {}]", logId);
		long startTime = System.nanoTime();
		PepResponse pepResponse = pepAgent.decide(parameters);
		PolicyEvaluationResult result = new PolicyEvaluationResult();
		result.setTimeInMs((System.nanoTime() - startTime) / 1000000);

		Result wrappedResult = pepResponse.getWrappedResult();
		result.setDecision(wrappedResult.getDecision().toString());
		Status status = new Status();
		result.setStatus(status);
		status.setStatusMessage(wrappedResult.getStatus().getStatusMessage());
		if (status.getStatusMessage() == null) {
			status.setStatusMessage("success");
		}
		Map<String, String> statusCode = new HashMap<>();
		statusCode.put("value", wrappedResult.getStatus().getStatusCode().getStatusCodeValue().toString());
		status.setStatusCode(statusCode);
		
		List<Obligation> obligations = new ArrayList<>(wrappedResult.getObligations().size()); 
		Iterator<org.apache.openaz.xacml.api.Obligation> obligationIterator = wrappedResult.getObligations().iterator();
		
		while(obligationIterator.hasNext()) {
			try{
				org.apache.openaz.xacml.api.Obligation ob = obligationIterator.next();
				Obligation obligation = new Obligation();
				obligation.setId(decodeURL(ob.getId().toString()));
				
				List<ObligationParameter> attrs = new ArrayList<>(ob.getAttributeAssignments().size());
				for (AttributeAssignment attributeAssignment : ob.getAttributeAssignments()) {
					ObligationParameter param = new ObligationParameter();
					param.setAttributeId(decodeURL(attributeAssignment.getAttributeId().toString()));
					List<String> attrValue = new ArrayList<>(1);
					attrValue.add(String.valueOf(attributeAssignment.getAttributeValue().getValue()));
					param.setValue(attrValue);
					param.setDataType("string");
					attrs.add(param);
				}
				obligation.setAttributeAssignment(attrs);
				obligations.add(obligation);
			} catch (UnsupportedEncodingException e) {
				log.error("error while decoding parameter or obligation name", e);
			}
		}
		result.setObligations(obligations);
		result.setLogId(logId);
		log.info("Request validated, [ Result: {}]", result);
		return result;
	}

	private Recipient createRecipientwithAttributes(PolicyValidationDTO policyValidationDTO) {
		Recipient recipient = null; // Recipient.newInstance();
		if (policyValidationDTO.getRecipient() != null) {
			for (Attribute attr : policyValidationDTO.getRecipient()) {
				recipient = Recipient.newInstance(attr.getAttributeValue());
			}
		}
		return recipient;
	}

	private Host createHostwithAttributes(PolicyValidationDTO policyValidationDTO) {
		Host host = Host.newInstance("localhost");
		if (policyValidationDTO.getHost() != null) {
			for (Attribute attr : policyValidationDTO.getHost()) {
				host = Host.newInstance(attr.getAttributeValue());
			}
		}
		return host;
	}

	private Environment createEnvironmentwithAttributes(PolicyValidationDTO policyValidationDTO, Long logId)
			throws ConsoleException {
		Environment environment = Environment.newInstance();
		boolean debug = policyValidationDTO.isDebug();
		log.debug("Validator debug flag value:{}", debug);
		if (debug) {
			environment.addAttribute("0_debugenabled", true);
			environment.addAttribute("0_log_id", logId);
		}
		if (policyValidationDTO.getEnvironment() != null) {
			for (Attribute attr : policyValidationDTO.getEnvironment()) {
				String[] attrValue = attr.getAttributeValue() != null ? new String[] { attr.getAttributeValue() }
						: attr.getListValue().toArray(new String[0]);
				environment.addAttribute(encodeURL(attr.getAttributeId()), attrValue);
			}
		}

		return environment;
	}

	private Application createApplicationwithAttributes(PolicyValidationDTO policyValidationDTO) {
		Application app = Application.newInstance("dummy-application");
		if (policyValidationDTO.getApplication() != null) {
			for (Attribute attr : policyValidationDTO.getApplication()) {
				if (attr.getAttributeId().equals("application-id")) {
					app.setApplicationID(attr.getAttributeValue());
				}
			}
		}
		return app;
	}

	private Resource createResourcewithAttributes(PolicyValidationDTO policyValidationDTO) throws ConsoleException {
		Resource resource = Resource.newInstance();
		if (policyValidationDTO.getResource() != null) {
			for (Attribute attr : policyValidationDTO.getResource()) {
				String[] attrValue = attr.getAttributeValue() != null ? new String[] { attr.getAttributeValue() }
						: attr.getListValue().toArray(new String[0]);
				String attrId = encodeURL(attr.getAttributeId());
				switch (attr.getAttributeId()) {
				case "resource-id":
					attrId = XACML3.ID_RESOURCE_RESOURCE_ID.toString();
					break;
				case "resource-type":
					attrId = com.nextlabs.openaz.utils.Constants.ID_RESOURCE_RESOURCE_TYPE.toString();
					break;
				default:
					break;
				}
				resource.addAttribute(attrId, attrValue);
			}
		}
		return resource;
	}

	private Action createActions(PolicyValidationDTO policyValidationDTO) throws ConsoleException {
		Action action = Action.newInstance();
		if (policyValidationDTO.getAction() != null) {
			for (Attribute attr : policyValidationDTO.getAction()) {
				String[] attrValue = attr.getAttributeValue() != null ? new String[] { attr.getAttributeValue() }
						: attr.getListValue().toArray(new String[0]);
				String attrId = "action-id".equals(attr.getAttributeId()) ? XACML3.ID_ACTION_ACTION_ID.toString()
						: encodeURL(attr.getAttributeId());
				action.addAttribute(attrId, attrValue);
			}
		}
		return action;
	}

	private Subject createSubjectwithAttributes(PolicyValidationDTO policyValidationDTO) throws ConsoleException {
		Subject subject = Subject.newInstance();
		if (policyValidationDTO.getSubject() != null) {
			for (Attribute attr : policyValidationDTO.getSubject()) {
				String[] attrValue = attr.getAttributeValue() != null ? new String[]{attr.getAttributeValue()}
						: attr.getListValue().toArray(new String[0]);
				String attrId = "subject-id".equals(attr.getAttributeId()) ? XACML1.ID_SUBJECT_SUBJECT_ID.toString()
						: String.format("%s.%s", ComponentPQLHelper.USER.toLowerCase(),
						encodeURL(attr.getAttributeId()));
				subject.addAttribute(attrId, attrValue);
			}
		}
		if (policyValidationDTO.getApplication() != null) {
			for (Attribute attr : policyValidationDTO.getApplication()) {
				String[] attrValue = attr.getAttributeValue() != null ? new String[]{attr.getAttributeValue()}
						: attr.getListValue().toArray(new String[0]);
				String attrId = encodeURL(attr.getAttributeId());
				subject.addAttribute(String.format("%s.%s", ComponentPQLHelper.APPLICATION.toLowerCase(), attrId), attrValue);
			}
		}
		if (policyValidationDTO.getHost() != null) {
			for (Attribute attr : policyValidationDTO.getHost()) {
				String[] attrValue = attr.getAttributeValue() != null ? new String[]{attr.getAttributeValue()}
						: attr.getListValue().toArray(new String[0]);
				String attrId = encodeURL(attr.getAttributeId());
				subject.addAttribute(String.format("%s.%s", ComponentPQLHelper.HOST.toLowerCase(), attrId), attrValue);
			}
		}
		return subject;
	}

	private final String decodeURL(String toDecode) throws UnsupportedEncodingException {
		return URLDecoder.decode(toDecode, Charset.forName("utf-8").toString());
	}

	private final String encodeURL(String toEncode) throws ConsoleException {
		try {
			return URLEncoder.encode(toEncode, Charset.forName("utf-8").toString());
		} catch (UnsupportedEncodingException e) {
			throw new ConsoleException(e);
		}
	}

	private List<String> findPqlFromPolicyIds(List<Long> ids, boolean forceEvaluate, boolean deployedOnly)
			throws ConsoleException {
		try {
			List<PolicyDevelopmentEntity> policies = new ArrayList<>();
			List<PolicyDevelopmentEntity> components = devEntityMgmtService
					.findByType(DevEntityType.COMPONENT.getKey());
			Iterator<PolicyDevelopmentEntity> iterator = components.iterator();
			while (iterator.hasNext()) {
				PolicyDevelopmentEntity entity = iterator.next();
				if (entity.getStatus().equals(DELETED.getKey())) {
					iterator.remove();
				}
			}

			List<PolicyDevelopmentEntity> devEntities = null;
			if (deployedOnly) {
				devEntities = policyDevelopmentEntityDao.findActiveRecordsByType(DevEntityType.POLICY.getKey());
			} else {
				devEntities = policyDevelopmentEntityDao.findActiveRecordsByType(DevEntityType.POLICY.getKey());
				// devEntities = new ArrayList<>(ids.size());
				// for (Long id : ids) {
				// devEntities.add(deploymentEntityMgmtService.findByType(developmentType))
				// }
			}

			Map<String, PolicyDevelopmentEntity> policyNameIdMap = getPolicyNameToIdMap(devEntities);
			Map<Long, PolicyDevelopmentEntity> policyIdMap = getPolicyMap(devEntities);
			Map<String, PolicyDevelopmentEntity> addedPolicyMap = new HashMap<>();

			for (Long id : ids) {
				PolicyDevelopmentEntity devEntity = policyIdMap.get(id);
				if (devEntity == null) {
					throw new ConsoleException("policy with id " + id + " not found");
				}

				String pql = devEntity.getPql();
				DomainObjectBuilder domBuilder = new DomainObjectBuilder(pql);

				try {
					IDPolicy policy = domBuilder.processPolicy();
					devEntity.setPolicy(policy);
					// if(addedPolicyMap.containsKey(devEntity.getTitle())) {
					// PolicyDevelopmentEntity entity =
					// addedPolicyMap.get(devEntity.getTitle());
					// IDPolicy idPolicy = entity.getPolicy();
					// if(idPolicy != null) {
					// IPolicyExceptions policyExceptions =
					// idPolicy.getPolicyExceptions();
					// }
					// continue;
					// }
					if (forceEvaluate) {
						policy.setAttribute(PolicyDTO.POLICY_EXCEPTION_ATTR, false);
						DomainObjectFormatter formatter = new DomainObjectFormatter();
						formatter.formatDef(policy);
						devEntity.setPql(formatter.getPQL());
					}
				} catch (Exception e) {
					throw new ConsoleException(e);
				}
				policies.add(devEntity);
				addAllSubPolicies(policies, policyNameIdMap, addedPolicyMap, devEntity);
			}

			DelegationRuleReferenceResolver resolver = DelegationRuleReferenceResolver.create(policies, components);
			List<IDPolicy> parsedPolicies = resolver.resolve();

			// remove log obligation from allow and deny
			for (IDPolicy p : parsedPolicies) {
				Iterator<IObligation> obligationIterator = p.getObligations(EffectType.ALLOW).iterator();
				while (obligationIterator.hasNext()) {
					IObligation iObligation = obligationIterator.next();
					if (iObligation instanceof LogObligation) {
						obligationIterator.remove();
					}
				}
				obligationIterator = p.getObligations(EffectType.DENY).iterator();
				while (obligationIterator.hasNext()) {
					IObligation iObligation = obligationIterator.next();
					if (iObligation instanceof LogObligation) {
						obligationIterator.remove();
					}
				}
			}

			String[] pqlList = DomainObjectFormatter.format(parsedPolicies);
			return Arrays.asList(pqlList);
		} catch (Exception e) {
			throw new ConsoleException("Error encountered in find policies for given ids", e);
		}
	}

	/**
	 * @param policies
	 * @param policyNameIdMap
	 * @param addedPolicyMap
	 * @param devEntity
	 * @return
	 * @throws ConsoleException
	 */
	private void addAllSubPolicies(List<PolicyDevelopmentEntity> policies,
			Map<String, PolicyDevelopmentEntity> policyNameIdMap, Map<String, PolicyDevelopmentEntity> addedPolicyMap,
			PolicyDevelopmentEntity devEntity) throws ConsoleException {
		IDPolicy policy = devEntity.getPolicy();
		if (policy != null) {
			IPolicyExceptions policyExceptions = policy.getPolicyExceptions();
			for (IPolicyReference policyRef : policyExceptions.getPolicies()) {
				String refName = policyRef.getReferencedName();
				// if(addedPolicyMap.containsKey(refName)) {
				// continue;
				// }
				PolicyDevelopmentEntity subDevEntity = policyNameIdMap.get(refName);
				if (subDevEntity == null)
					continue;
				policies.add(subDevEntity);
				String pql = subDevEntity.getPql();
				try {
					subDevEntity.setPolicy(new DomainObjectBuilder(pql).processPolicy());
					addAllSubPolicies(policies, policyNameIdMap, addedPolicyMap, subDevEntity);
				} catch (Exception e) {
					throw new ConsoleException(e);
				}
			}
		}
	}

	private List<String> getPqlFromPolicySearchCriteria(List<SearchField> searchFields) throws ConsoleException {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setFields(searchFields);
		criteria.setPageNo(0);
        criteria.setPageSize(100000);
		boolean deployedOnly = false;
		for (SearchField f : searchFields) {
			StringFieldValue stringFieldValue;
			if (f.getField().equals("status") && f.getValue() instanceof StringFieldValue) {
				stringFieldValue = (StringFieldValue) f.getValue();
				deployedOnly = stringFieldValue.getValue() instanceof List
						&& stringFieldValue.getValue().equals(DEPLOYED_ONLY_STATUS);
			}
		}

		Iterator<PolicyLite> iterator = policySearchService.findPolicyByCriteria(criteria).iterator();

		List<Long> ids = new ArrayList<>();
		while (iterator.hasNext()) {
			ids.add(iterator.next().getId());
		}
		log.debug("{} policies found, [{}]", ids.size(), ids);
		return findPqlFromPolicyIds(ids, false, deployedOnly);
	}

	private Map<String, PolicyDevelopmentEntity> getPolicyNameToIdMap(List<PolicyDevelopmentEntity> devEntities) {
		Map<String, PolicyDevelopmentEntity> policyNameIdMap = new HashMap<>();
		for (PolicyDevelopmentEntity devEntity : devEntities) {
			policyNameIdMap.put(devEntity.getTitle(), devEntity);
		}
		return policyNameIdMap;
	}

	private Map<Long, PolicyDevelopmentEntity> getPolicyMap(List<PolicyDevelopmentEntity> devEntities) {
		Map<Long, PolicyDevelopmentEntity> policyIdMap = new HashMap<>();
		for (PolicyDevelopmentEntity devEntity : devEntities) {
			policyIdMap.put(devEntity.getId(), devEntity);
		}
		return policyIdMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nextlabs.destiny.console.services.policy.PolicyValidatorService#
	 * getValidationLogs(long)
	 */
	@Override
	public List<GenericEvaluationLog> getValidationLogs(String logId) throws ConsoleException {
		List<String> cachedLogsList = FileHandlerForPolicyEvaluation.getCache(logId);
		if (cachedLogsList == null) {
			throw new NoDataFoundException(msgBundle.getText("no.data.found.code"), msgBundle.getText("no.data.found"));
		} else {
			log.debug("Policy evaluation logs, [ No of cached logs :{}]", cachedLogsList.size());
			List<GenericEvaluationLog> logList = new ArrayList<>();
			for (String cachedLog : cachedLogsList) {
				String[] splits = cachedLog.split(LOG_DELIMITER);
				if(splits.length >= 2) {
					long timestamp = Long.valueOf(splits[0]);
					String msgContent = splits[1];
					GenericEvaluationLog evalLog = getLog(timestamp, msgContent);
					logList.add(evalLog);
				} else {
					continue;
				}
			}
			return logList;
		}
	}

	private GenericEvaluationLog getLog(Long timestamp, String msgContent) {
		log.debug("Generic Evaluation Log content, [ Time:{}, Msg Content: {}]", timestamp, msgContent);
		GenericEvaluationLog evalLog = new GenericEvaluationLog();
		evalLog.setTimestamp(timestamp);
		evalLog.setContent(msgContent);

		Matcher matchingPolicyLogMatcher = Pattern.compile("Matching policies for\\D*(\\d+).*").matcher(msgContent);
		if (matchingPolicyLogMatcher.find()) {
			Map<String, List<String>> matchingPolicyGrid = new HashMap<String, List<String>>();
			String[] splits = Pattern.compile("\\n", Pattern.CASE_INSENSITIVE).split(msgContent);

			for (String line : splits) {
				String decision = line.trim().substring(0, 1);
				String policyName = line.trim().substring(2);
				if (decision.equalsIgnoreCase("D")) {
					decision = "DENY";
				} else if (decision.equalsIgnoreCase("A")) {
					decision = "ALLOW";
				} else if (decision.equalsIgnoreCase("X")) {
					decision = "NO_MATCH";
				} else {
					continue;
				}

				List<String> matchPolicies = matchingPolicyGrid.get(decision);
				if (matchPolicies == null) {
					matchPolicies = new ArrayList<>();
					matchingPolicyGrid.put(decision, matchPolicies);
				}
				matchPolicies.add(policyName);
			}
			evalLog.setMappingDetails(true);
			evalLog.setMappingPolicyDetails(matchingPolicyGrid);
			log.debug("Has a matching policy log  INSIDE -->>> {}", evalLog);
		} else {
			evalLog.setMappingDetails(false);
		}
		log.info("GenericEvaluationLog data: {}", evalLog.toString());
		return evalLog;
	}

	public long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / 8);
		buffer.put(bytes);
		buffer.flip();// need flip
		return buffer.getLong();
	}

	public int bytesToInt(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / 8);
		buffer.put(bytes);
		buffer.flip();// need flip
		return buffer.getInt();
	}
}
