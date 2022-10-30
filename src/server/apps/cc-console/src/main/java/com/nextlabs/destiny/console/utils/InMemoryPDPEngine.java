/**
 * 
 */
package com.nextlabs.destiny.console.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Obligation;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.Response;
import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.api.pdp.PDPEngine;
import org.apache.openaz.xacml.api.pdp.PDPException;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdAttributeAssignment;
import org.apache.openaz.xacml.std.StdAttributeValue;
import org.apache.openaz.xacml.std.StdObligation;
import org.apache.openaz.xacml.std.StdResponse;
import org.apache.openaz.xacml.std.StdResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bluejungle.destiny.agent.pdpapi.IPDPNamedAttributes;
import com.bluejungle.framework.expressions.EvalValue;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.ValueType;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.pf.domain.destiny.action.DAction;
import com.bluejungle.pf.domain.destiny.action.IDAction;
import com.bluejungle.pf.domain.destiny.obligation.CustomObligation;
import com.bluejungle.pf.domain.destiny.obligation.DisplayObligation;
import com.bluejungle.pf.domain.destiny.obligation.IDObligation;
import com.bluejungle.pf.domain.destiny.obligation.LogObligation;
import com.bluejungle.pf.domain.destiny.obligation.NotifyObligation;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.destiny.subject.IDSubject;
import com.bluejungle.pf.domain.destiny.subject.Subject;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;
import com.bluejungle.pf.domain.epicenter.resource.IResource;
import com.bluejungle.pf.domain.epicenter.resource.Resource;
import com.bluejungle.pf.domain.epicenter.subject.ISubjectType;
import com.bluejungle.pf.engine.destiny.EngineResourceInformation;
import com.bluejungle.pf.engine.destiny.EvaluationEngine;
import com.bluejungle.pf.engine.destiny.EvaluationFailureResponse;
import com.bluejungle.pf.engine.destiny.EvaluationRequest;
import com.bluejungle.pf.engine.destiny.EvaluationResult;
import com.bluejungle.pf.engine.destiny.IEngineSubjectResolver;
import com.bluejungle.pf.engine.destiny.PolicyEvaluationException;
import com.nextlabs.destiny.console.delegadmin.helpers.AppUserSubject;
import com.nextlabs.destiny.console.delegadmin.helpers.DelegationTargetResolver;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.openaz.pdp.beans.PDPRequest;
import com.nextlabs.openaz.pdp.utils.PDPRequestUtil;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
@Configuration
public class InMemoryPDPEngine implements PDPEngine {

    private static final Logger log = LoggerFactory.getLogger(InMemoryPDPEngine.class);
    
    private static final Logger validationLog = LoggerFactory.getLogger("policy-evaluation");
    public static final String ATTRIBUTE_APPLICATION_ID = "application-id";
    public static final String ATTRIBUTE_INET_ADDRESS = "inet_address";
    public static final String ATTRIBUTE_HOST = "host";

    private static InMemoryPDPEngine engineInstance;

    private static final List<URI> EMPTY_PROFILES = new ArrayList<URI>(0);

    private static final String ISSUER = "nextlabs.com";

    private static final Identifier ATTRIBUTE_CATEGORY = new IdentifierImpl("obligation-attribute");
    
    private DelegationTargetResolver resolver = null;
    
    private EvaluationEngine engine = null;

    public InMemoryPDPEngine() {
        super();
    }

    private InMemoryPDPEngine(Properties properties) {
        super();
        this.resolver = new DelegationTargetResolver(new IDPolicy[0]);
        this.engine = new EvaluationEngine(resolver);
    }

    @Bean
    public static InMemoryPDPEngine getInstance(Properties properties) throws PDPException {
        if (engineInstance == null) {
            synchronized (InMemoryPDPEngine.class) {
                if (engineInstance == null) {
                    engineInstance = new InMemoryPDPEngine(properties);
                }
            }
        }
        return engineInstance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.openaz.xacml.api.pdp.PDPEngine#decide(org.apache.openaz.xacml.
     * api.Request)
     */
    @Override
    public Response decide(Request req) throws PDPException {
        List<PDPRequest> pdpRequests = PDPRequestUtil.convert(req);
        List<Result> results = new ArrayList<>(pdpRequests.size());
        for (PDPRequest pdpReq : pdpRequests) {
            try {
                results.add(decide(pdpReq));
            } catch (UnsupportedEncodingException e) {
                log.error("Error while evaluating", e);
            } catch (PolicyEvaluationException e) {
                throw new PDPException(e);
            }
        }
        return new StdResponse(results);
    }

    /**
     * @param pdpReq
     * @param defaultPolicies
     * @return
     * @throws UnsupportedEncodingException 
     */
    private Result decide(PDPRequest pdpReq) throws UnsupportedEncodingException, PolicyEvaluationException {
        long enterTime = System.nanoTime();
        IDAction idAction = DAction.getAction(pdpReq.getAction());
        IResource fromResource = null;
        if (pdpReq.getResource() != null) {
            Map<String, IEvalValue> attrs = new HashMap<>();
            Map<String, DynamicAttributes> resourceAttributes = new HashMap<>();
            pdpReq.getResource().addSelfToMap(resourceAttributes);
            for (Map.Entry<String, DynamicAttributes> attributes : resourceAttributes.entrySet()) {
                for (Map.Entry<String, IEvalValue> attribute : attributes.getValue().entrySet()) {
                    attrs.put(decodeURL(attribute.getKey()), SerializationUtils.clone(attribute.getValue()));
                }
            }
            fromResource = new Resource(pdpReq.getResource().getValue("ce::id"), attrs);
        }
        EngineResourceInformation fromResourceInformation = new EngineResourceInformation();
        DynamicAttributes applicationDynaAttrs = new DynamicAttributes();
        DynamicAttributes hostDynaAttrs = new DynamicAttributes();
        DynamicAttributes userDynaAttrs = new DynamicAttributes();

        // Subject with attributes
        String userName = null;
        IDSubject subject = null;
        if (pdpReq.getUser() != null) {
            userName = pdpReq.getUser().getName();
            Map<String, DynamicAttributes> userAttributes = new HashMap<>();
            pdpReq.getUser().addSelfToMap(userAttributes);
            for (Map.Entry<String, DynamicAttributes> attributes : userAttributes.entrySet()) {
                for (Map.Entry<String, IEvalValue> attribute : attributes.getValue().entrySet()) {
                    String[] attributeNameSections = attribute.getKey().split("\\.");
                    if (attributeNameSections.length > 1) {
                        String type = attributeNameSections[0];
                        String name = attributeNameSections[1];
                        if (ComponentPQLHelper.APPLICATION.toLowerCase().equals(type)) {
                            applicationDynaAttrs.put(decodeURL(name), SerializationUtils.clone(attribute.getValue()));
                        } else if (ComponentPQLHelper.HOST.toLowerCase().equals(type)) {
                            hostDynaAttrs.put(decodeURL(name), SerializationUtils.clone(attribute.getValue()));
                        } else {
                            userDynaAttrs.put(decodeURL(name), SerializationUtils.clone(attribute.getValue()));
                        }
                    }
                }
            }
            subject = new AppUserSubject(pdpReq.getUser().getValue("id"), pdpReq.getUser().getValue("id"),
                    userName, -1L, SubjectType.APPUSER, userDynaAttrs);

        }
        IDSubject loggedInUser = subject;

        IDSubject application = null;
        if (!applicationDynaAttrs.isEmpty()) {
            String applicationId = applicationDynaAttrs.getString(ATTRIBUTE_APPLICATION_ID);
            applicationDynaAttrs.remove(ATTRIBUTE_APPLICATION_ID);
            if (StringUtils.isNotEmpty(applicationId)) {
                application = new AppUserSubject(applicationId, applicationId, applicationId, -1L, SubjectType.APP,
                        applicationDynaAttrs);
            }
        } else if (pdpReq.getApplication() != null) {
                application = new Subject(pdpReq.getApplication().getName(), pdpReq.getApplication().getName(), pdpReq.getApplication().getName(), -1L,
                        SubjectType.APP);
        }

        IDSubject[] sendTo = null;
        IPDPNamedAttributes sendToAttr = pdpReq.getNamedAttributes("sendto");
        if (sendToAttr != null) {
            sendTo = new Subject[] { new Subject(sendToAttr.getValue("email"), SubjectType.USER) };
        }

        String hostName = null;
        if (!hostDynaAttrs.isEmpty()) {
            hostName = hostDynaAttrs.getString(ATTRIBUTE_INET_ADDRESS);
            hostDynaAttrs.remove(ATTRIBUTE_INET_ADDRESS);
            if (StringUtils.isEmpty(hostName)) {
                hostName = hostDynaAttrs.getString(ATTRIBUTE_HOST);
            }
        } else if (pdpReq.getHost() != null) {
            hostName = pdpReq.getHost().getValue(ATTRIBUTE_INET_ADDRESS);
            if (StringUtils.isEmpty(hostName)) {
                hostName = pdpReq.getHost().getValue(ATTRIBUTE_HOST);
            }
        }

        IDSubject host = null;
        if (StringUtils.isNotEmpty(hostName)) {
            host = new AppUserSubject(hostName, hostName, hostName, -1L, SubjectType.HOST, hostDynaAttrs);
        }

        DynamicAttributes env = null;
        boolean debug = false;
        IPDPNamedAttributes envAttr = pdpReq.getNamedAttributes("environment");
        Long logId = 0L;
        if (envAttr != null) {
            env = new DynamicAttributes();
            for (String key : envAttr.keySet()) {
                env.add(decodeURL(key), envAttr.getValue(key));
                if(key.equals("0_debugenabled")) {
                    debug = Boolean.parseBoolean(envAttr.getValue(key));
                } else if(key.equals("0_log_id")) {
                    logId = Long.parseLong(envAttr.getValue(key));
                }
            }
        }
        if(logId == 0) {
            logId = PolicyValidationIdGenerator.generateId();
        }

        String pql = null;
        IPDPNamedAttributes policiesAttr = pdpReq.getNamedAttributes("policies");
        boolean ignoreBuiltinPolicies = false;
        if (policiesAttr != null) {
            pql = policiesAttr.getValue("pql");
            ignoreBuiltinPolicies = "yes".equalsIgnoreCase(policiesAttr.getValue("ignoredefault"));
        }

        IEngineSubjectResolver subjectResolver = null;
        new IEngineSubjectResolver() {

            @Override
            public boolean existsSubject(String paramString, ISubjectType paramISubjectType) {
                return true;
            }

            @Override
            public IEvalValue getGroupsForSubject(String paramString, ISubjectType paramISubjectType) {
                return new EvalValue(ValueType.STRING, "Dummy-group");
            }

        };

        EvaluationRequest evalRequest = new EvaluationRequest(logId, idAction,
                                                              fromResource, fromResourceInformation,
                                                              subject, userName, subjectResolver,
                                                              application,
                                                              host, hostName,
                                                              env,
                                                              pql, ignoreBuiltinPolicies,
                                                              true, 0, 0);

        evalRequest.setSentTo(sendTo);
        
        // call customized logger to log request
        StringBuilder inputParams = new StringBuilder();
        if (debug && validationLog.isInfoEnabled()) {
            Map<String, DynamicAttributes> context = new HashMap<>();
            if (subject != null) {
                context.put("subject", new DynamicAttributesBuilder().build(subject).build());
            }
            if (application != null) {
                context.put("application", new DynamicAttributesBuilder().build(application).build());
            }
            if (host != null) {
                context.put("host", new DynamicAttributesBuilder().build(host).build());
            }
            if (idAction != null) {
                context.put("action", new DynamicAttributesBuilder().build(idAction).build());
            }
            if (fromResource != null) {
                context.put("fromResource", new DynamicAttributesBuilder().build(fromResource).build());
            }
            if (sendTo != null) {
                context.put("sendto", new DynamicAttributesBuilder().buildSendTo(sendTo).build());
            }
            if (env != null) {
                context.put("environment", new DynamicAttributesBuilder().buildEnvAttributes(env).build());
            }
            context.put("policies", new DynamicAttributesBuilder().build("ignoredefault", ignoreBuiltinPolicies).build());
            inputParams = this.inputParamsToString(evalRequest.getRequestId(), context, 0L, 0, false);
        }
        long querySetupTime = (System.nanoTime() - enterTime)/1000000;

        EvaluationResult result = engine.evaluate(evalRequest);
        
        long obligationStart = System.nanoTime();
        Collection<Obligation> obligations = buildObligations(result.getObligations(), evalRequest);
        long obligationExecutionTime = (System.nanoTime() - obligationStart)/1000000;
        
        if(debug && validationLog.isInfoEnabled()) {
            long elapsedTime = (System.nanoTime() - enterTime)/1000000;
            inputParams.append("  Result: Effect = ");
            inputParams.append(result.getEffectName());
            inputParams.append(" (total:");
            inputParams.append(elapsedTime);
            inputParams.append("ms, setup:");
            inputParams.append(querySetupTime);
            inputParams.append("ms, obligations:");
            inputParams.append(obligationExecutionTime);
            inputParams.append("ms)\n");
            
            if (!obligations.isEmpty()) {
                inputParams.append("  Obligations:\n");
                for (Obligation obligation: obligations) {
                    try {
                        inputParams.append(URLDecoder.decode(obligation.getId().getUri().toString(), Charset.forName("utf-8").toString()));
                        for(AttributeAssignment param: obligation.getAttributeAssignments()) {
                            inputParams.append(" ").append(param.getAttributeValue());
                        }
                    } catch (UnsupportedEncodingException e) {
                        log.error("Error while decoding obligation name");
                    }
                }
            }
            validationLog.info(inputParams.toString());
        }
        
        
        StdResult stdResult = new StdResult(buildDecision(result.getEffectName()), obligations, null, null, null,
                                            null);
        return stdResult;
    }

    private final String decodeURL(String toDecode) throws UnsupportedEncodingException {
        return URLDecoder.decode(toDecode, Charset.forName("utf-8").toString());
    }
    
    /**
     * @param obligations
     * @param evalRequest
     * @return
     * @throws UnsupportedEncodingException
     */
    private Collection<Obligation> buildObligations(List<IDObligation> obligations, EvaluationRequest evalRequest) {
        Collection<Obligation> obligationList = new ArrayList<>(obligations.size());
        for (IDObligation obli : obligations) {
            if (obli instanceof NotifyObligation) {
                obligationList.add(new StdObligation(new IdentifierImpl(NotifyObligation.OBLIGATION_NAME)));
            } else if (obli instanceof DisplayObligation) {
                obligationList.add(new StdObligation(new IdentifierImpl(DisplayObligation.OBLIGATION_NAME)));
            } else if (obli instanceof LogObligation) {
                obligationList.add(new StdObligation(new IdentifierImpl(LogObligation.OBLIGATION_NAME)));
            } else if (obli instanceof CustomObligation) {
                CustomObligation customObli = (CustomObligation) obli;
                Collection<AttributeAssignment> attributeAssignments = new ArrayList<>(customObli.getCustomObligationArgs().size());
                List<? extends Object> customObligationArgs = customObli.getCustomObligationArgs();
                for (int index = 0; index < customObligationArgs.size(); index += 2) {
                    if (index == customObligationArgs.size() - 1)
                        break;
                    String paramName = String.valueOf(customObligationArgs.get(index));
                    String paramValue = String.valueOf(customObligationArgs.get(index + 1));
                    try {
                        AttributeAssignment attrAssignment = null;
                        if (paramValue != null && paramValue.startsWith("$")) {
                            paramValue = convertObligationParam(paramValue, evalRequest);
                        }
                        attrAssignment = new StdAttributeAssignment(ATTRIBUTE_CATEGORY,
                                new IdentifierImpl(URLEncoder.encode(paramName, Charset.forName("utf-8").toString())), ISSUER,
                                new StdAttributeValue<String>(XACML3.ID_DATATYPE_STRING, paramValue));
                        attributeAssignments.add(attrAssignment);
                    } catch (UnsupportedEncodingException e) {
                        log.error("Exception while assembling obligation attributes", e);
                    }
                }
                try {
                    obligationList.add(
                            new StdObligation(new IdentifierImpl(URLEncoder.encode(customObli.getCustomObligationName(), Charset.forName("utf-8").toString())),
                                    attributeAssignments));
                } catch (UnsupportedEncodingException e) {
                    log.error("Exception while assembling obligation", e);
                }
            }
        }
        return obligationList;
    }

    private String convertObligationParam(String paramValue, EvaluationRequest evalRequest) {
        String[] attrIdentifier = paramValue.substring(1).split("\\.");
        if (attrIdentifier.length != 2) {
            return paramValue;
        }
        String dimension = attrIdentifier[0];
        String attr = attrIdentifier[1];

        IEvalValue v = EvalValue.NULL;
        if (dimension.equals("environment")) {
            v = evalRequest.getEnvironment().get(attr);
        } else if (dimension.equals("from")) {
            IResource from = evalRequest.getFromResource();
            v = from.getAttribute(attr);
        } else if (dimension.equals("to")) {
            IResource to = evalRequest.getToResource();
            if (to != null) {
                v = to.getAttribute(attr);
            }
        } else if (dimension.equals("user")) {
            IDSubject user = evalRequest.getUser();
            v = user.getAttribute(attr);
        } else {
            return paramValue;
        }

        if (v != null && v.getValue() instanceof String) {
            return (String) v.getValue();
        }
        return paramValue;
    }

    private Decision buildDecision(String result) {
        if (result.equals(EvaluationResult.ALLOW)) {
            return Decision.PERMIT;
        } else if (result.equals(EvaluationResult.DENY)) {
            return Decision.DENY;
        } else {
            return Decision.NOTAPPLICABLE;
        }
    }

    /**
     * Convert input parameters to a string (to be printed later)
     * 
     * @param paramArray
     *            Parameter Array from C++ callback
     * @return void
     */
    private StringBuilder inputParamsToString(Long sequenceID, Map<String, DynamicAttributes> context, Long processToken, int loggingLevel, boolean ignoreObligations) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Request ");
        buffer.append(sequenceID);
        buffer.append(" input params\n");
        
        Set<String> keys = context.keySet();
        for (String name : keys) {
            buffer.append("  ");
            buffer.append(name);
            buffer.append("\n");
            Set<String> dynKeys = context.get(name).keySet();

            for (String key : dynKeys) {
                String[] vals = context.get(name).getStrings(key);

                buffer.append("\t");
                buffer.append(key);
                buffer.append(":");
                for (String v : vals) {
                    buffer.append(" ");
                    buffer.append(v);
                }
                buffer.append("\n");
            }
        }
        
        buffer.append("  Ignore obligation = ");
        buffer.append(Boolean.toString(ignoreObligations));
        buffer.append("\n");
        buffer.append("  Process Token = ");
        buffer.append(processToken);
        buffer.append("\n");
        buffer.append("  LogLevel = ");
        buffer.append(loggingLevel);
        buffer.append("\n");
        return buffer;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.openaz.xacml.api.pdp.PDPEngine#getProfiles()
     */
    @Override
    public Collection<URI> getProfiles() {
        return EMPTY_PROFILES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.openaz.xacml.api.pdp.PDPEngine#hasProfile(java.net.URI)
     */
    @Override
    public boolean hasProfile(URI arg0) {
        return false;
    }
}
