/*
 * Created on May 3, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl;

import com.bluejungle.destiny.inquirycenter.enumeration.ReportLoggingLevelUIType;
import com.bluejungle.destiny.inquirycenter.report.IReportDetailResult;
import com.bluejungle.destiny.types.effects.v1.EffectType;
import com.bluejungle.destiny.types.report_result.v1.DocumentActivityDetailResult;
import com.bluejungle.destiny.types.report_result.v1.PolicyActivityDetailResult;
import com.bluejungle.destiny.webui.framework.faces.UIActionType;
import com.bluejungle.destiny.webui.framework.faces.UIEnforcementType;
import com.bluejungle.domain.action.ActionEnumType;
import com.bluejungle.domain.policydecision.PolicyDecisionEnumType;

import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * This class contains a report detail result record values. It wraps around a
 * web service data object and exposes its values to the presentation layer.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/bluejungle/destiny/inquirycenter/report/defaultimpl/ReportDetailResultImpl.java#1 $
 */
public class ReportDetailResultImpl implements IReportDetailResult {

    /**
	 * if resource match this string, replace with empty string
	 */
	protected static final String NO_ATTACHMENT_STR = "[no attachment]";
	
	/**
     * File path separator
     */
    private static final String PATH_SEPARATOR = "/";
    private static final String WINDOWS_PATH_SEPARATOR = "\\";
    
    private static final String HTML_WBR = "<wbr>";//"&#8203;"; 

    /**
     * Policy folder separator
     */
    private static final String POLICY_FOLDER_SEPARATOR = "/";

    private static final String DOUBLE_PATH_SEPARATOR = PATH_SEPARATOR + PATH_SEPARATOR;
    private static final String LOCALHOST_INSERTION = PATH_SEPARATOR + "localhost" + PATH_SEPARATOR;
    private static final String FILE_URL_SCHEMA_PREFIX = "file://";

    private static final int MAX_RESOURCE_DISPLAY_SIZE 		= 98;
    private static final int MAX_USER_DISPLAY_SIZE 			= 44;
    private static final int MAX_POLICY_DISPLAY_SIZE 		= 52;
    private static final int MAX_HOST_DISPLAY_SIZE 			= 58;
    private static final int MAX_APPLICATION_DISPLAY_SIZE 	= 70;
    private long id;
    private String action;
    private String applicationName;
    private Date date;
    private String enforcement;
    private String fromResourceFilename;
    private String fromResourcePath;
    private String fromResource;
    private String fromResourceShortened;
    private String hostIPAddress;
    private String hostName;
    private String policyFolderName;
    private String policyName;
    private String toResourceFilename;
    private String toResourcePath;
    private String toResource;
    private String user;
    private int loggingLevel;
    private String loggingLevelDisplay;

    /**
     * 
     * Constructor to be used when converting a tracking detail result
     * 
     * @param wsTrackingDetailResult
     *            tracking detail result to display
     */
    public ReportDetailResultImpl(DocumentActivityDetailResult wsTrackingDetailResult) {
        if (wsTrackingDetailResult == null) {
            throw new NullPointerException("wsTrackingDetailResult cannot be null");
        }
        this.id = wsTrackingDetailResult.getId().getId();
        setAction(wsTrackingDetailResult.getAction().getActionType());
        this.applicationName = wsTrackingDetailResult.getApplicationName();
        this.date = wsTrackingDetailResult.getTimestamp().getTime();
        setFromResourceName(wsTrackingDetailResult.getFromResourceName());
        this.hostIPAddress = wsTrackingDetailResult.getHostIPAddress();
        this.hostName = wsTrackingDetailResult.getHostName();
        setToResourceName(wsTrackingDetailResult.getToResourceName());
        this.user = wsTrackingDetailResult.getUserName();
        this.loggingLevel = wsTrackingDetailResult.getLoggingLevel();
        setLoggingLevelDisplay();
    }

    /**
     * 
     * Constructor to be used when converting a tracking detail result
     * 
     * @param wsPolicyDetailResult
     *            policy detail result to display
     */
    public ReportDetailResultImpl(PolicyActivityDetailResult wsPolicyDetailResult) {
        if (wsPolicyDetailResult == null) {
            throw new NullPointerException("wsPolicyDetailResult cannot be null");
        }
        this.id = wsPolicyDetailResult.getId().getId();
        setAction(wsPolicyDetailResult.getAction().getActionType());
        this.applicationName = wsPolicyDetailResult.getApplicationName();
        this.date = wsPolicyDetailResult.getTimestamp().getTime();
        setEnforcement(wsPolicyDetailResult.getEffect());
        setFromResourceName(wsPolicyDetailResult.getFromResourceName());
        this.hostIPAddress = wsPolicyDetailResult.getHostIPAddress();
        this.hostName = wsPolicyDetailResult.getHostName();
        setPolicyName(wsPolicyDetailResult.getPolicyName());
        setToResourceName(wsPolicyDetailResult.getToResourceName());
        this.user = wsPolicyDetailResult.getUserName();
        this.loggingLevel = wsPolicyDetailResult.getLoggingLevel();
        setLoggingLevelDisplay();
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getId()
     */
    public long getId() {
        return this.id;
    }
    
    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getAction()
     */
    public String getAction() {
        String result = null;
        // NOTE: this is added due to Bug4213
        if (this.action.equals("Embed")){
            result = "Copy";
        } else {
            result = this.action;
        }
        return result;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getApplicationName()
     */
    public String getApplicationName() {
        return this.applicationName;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getDate()
     */
    public Date getDate(){
        return this.date;
    }
    
    public String getDateString() {
        return DateFormat.getDateInstance().format(this.date);
    }    

    public String getTimeString() {
        return DateFormat.getTimeInstance().format(this.date);
    }
    

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getEnforcement()
     */
    public String getEnforcement() {
        return this.enforcement;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getFromResourceName()
     */
    public String getFromResourceFilename() {
        return this.fromResourceFilename;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getFromResourcePath()
     */
    public String getFromResourcePath() {
        return this.fromResourcePath;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getFromResource()
     * @return an escaped string, may contain <wbr>
     */
    public String getFromResource() {
        String processedFromResource = null;
        if (this.fromResource.length() > (MAX_RESOURCE_DISPLAY_SIZE/2)){
            processedFromResource = forHTMLTag(this.fromResource.substring(0, MAX_RESOURCE_DISPLAY_SIZE/2) )+
            						HTML_WBR +
            						forHTMLTag(this.fromResource.substring(MAX_RESOURCE_DISPLAY_SIZE/2, this.fromResource.length()));
            return processedFromResource;
        } else {
            return forHTMLTag(this.fromResource);
        }
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getHost()
     */
    public String getHost() {
        return this.hostName;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getHostIPAddress()
     */
    public String getHostIPAddress() {
        return this.hostIPAddress;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getPolicyName()
     */
    public String getPolicyName() {
        return this.policyName;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getPolicyFolderName()
     */
    public String getPolicyFolderName() {
        return this.policyFolderName;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getPolicyFullName()
     * @return an escaped string, may contain <wbr>
     */
    public String getPolicyFullName() {
        String result = null;
        final String folderName = getPolicyFolderName();
        final String name = getPolicyName();
        if (folderName != null && name != null) {
            result = getPolicyFolderName() + POLICY_FOLDER_SEPARATOR + getPolicyName();
            if (result.length() > (MAX_POLICY_DISPLAY_SIZE/2)){
                String processedPolicy = forHTMLTag(result.substring(0, MAX_POLICY_DISPLAY_SIZE/2)) +
                						HTML_WBR +
                						forHTMLTag(result.substring(MAX_POLICY_DISPLAY_SIZE/2, result.length()));
                result = processedPolicy;
            }else{
            	result = forHTMLTag(result);
            }
        }
        return result;
    }
    
    /**
     * @return an escaped string, may contain <wbr>
     */
    public String getPolicyPrintName(){
        String result = "";
        final String folderName = getPolicyFolderName();
        final String name = getPolicyName();
        if (folderName != null && name != null) {
            result = getPolicyFolderName() + POLICY_FOLDER_SEPARATOR + getPolicyName();
            String processedPolicy = "";
            for (int i = 0; i < 4; i++){
                String subString = result.substring(MAX_POLICY_DISPLAY_SIZE/2*i);
                if (subString.length() > MAX_POLICY_DISPLAY_SIZE/2){
                    processedPolicy += forHTMLTag(result.substring(MAX_POLICY_DISPLAY_SIZE/2*i, MAX_POLICY_DISPLAY_SIZE/2*(i+1)));
                    processedPolicy += HTML_WBR;
                } else {
                    processedPolicy += forHTMLTag(subString);
                    break;
                }
            }
            result = processedPolicy;
        }
        return result;
    }


    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getToResourceFilename()
     */
    public String getToResourceFilename() {
        return this.toResourceFilename;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getToResourcePath()
     */
    public String getToResourcePath() {
        return this.toResourcePath;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getToResourceName()
     */
    public String getToResource() {
        return this.toResource;
    }

    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getUser()
     */
    public String getUser() {
        return this.user;
    }

    
    /**
     * @see com.bluejungle.destiny.inquirycenter.report.IReportDetailResult#getLoggingLevel()
     */
    public int getLoggingLevel() {
        return this.loggingLevel;
    }
    
    /**
     * 
     * @return
     */
    public String getLoggingLevelDisplay() {
        return this.loggingLevelDisplay;
    }    
        
    /**
     * Returns the shortened version of the username
     * 
     * @return the shortened version of the username
     */
    public String getUserShortened() {
        if (this.user.length() > MAX_USER_DISPLAY_SIZE) {
            return this.user.substring(0, MAX_USER_DISPLAY_SIZE - 4) + "...";
        } else {
            return this.user;
        }
    }
    
    /**
     * Returns the shortened version of the policy name
     * 
     * @return the shortened version of the policy name, the string is escaped and may contain <wbr>
     */
    public String getPolicyShortened() {
		//we should only process an unescaped string
        String result = forUnescapedHTML(this.getPolicyFullName());
        result = result.replace("<wbr>", "");
        
        // First process the ellipsis " ... "
        if (result.length() > MAX_POLICY_DISPLAY_SIZE) {
            int lastPathSeparatorIndex = result.lastIndexOf("/");
            String policyName = null;
            String policyPath = null;
            if (lastPathSeparatorIndex > -1){
                policyName = result.substring(lastPathSeparatorIndex, result.length());
                policyPath = result.substring(0, lastPathSeparatorIndex);
            } else {
                policyName = result;
                policyPath = "";
            }
            if (result.length() > MAX_POLICY_DISPLAY_SIZE){ // we need to shorten the application
                // shorten by the length of " ... ", which is 5 characters
                int shortenByLength = 5;
                if (policyPath.length() > shortenByLength  &&
                    policyName.length() < (MAX_POLICY_DISPLAY_SIZE - shortenByLength)){ 
                    // the path is long enough to be shorten and the application name is short enough 
                    // that it makes sense to shorten the path
                    result = policyPath.substring(0, MAX_POLICY_DISPLAY_SIZE - policyName.length() - shortenByLength) + 
                    " ... " + 
                    policyName;
                } else {
                    // either the path is not long enough to be shorten or the application name itself 
                    // is too long.  In this case, just display the application name
                    result = policyName.length() > MAX_POLICY_DISPLAY_SIZE ? 
                             policyName.substring(0, MAX_POLICY_DISPLAY_SIZE - shortenByLength) + " ... " :
                             policyName;
                }
            }
        }
        
        // Then insert a space for wrapping
        if (result.length() > (MAX_POLICY_DISPLAY_SIZE/2)){
            String processedPolicy = forHTMLTag(result.substring(0, MAX_POLICY_DISPLAY_SIZE / 2)) +
            						 HTML_WBR +
            						 forHTMLTag(result.substring(MAX_POLICY_DISPLAY_SIZE / 2, result.length()));
            result = processedPolicy;
        }else{
        	result = forHTMLTag(result);
        }
        return result;
    }
    
    /**
     * Returns the shortened version of the host name
     * 
     * @return the shortened version of the host name
     */
    public String getHostShortened() {
        if (this.hostName.length() > MAX_HOST_DISPLAY_SIZE) {
            return this.hostName.substring(0, MAX_HOST_DISPLAY_SIZE - 4) + "...";
        } else {
            return this.hostName;
        }
    }
    
    /**
     * Returns the shortened version of the application name
     * 
     * @return the shortened version of the application name,  the string is escaped and may contain <wbr>
     */
    public String getApplicationShortened() {
        String result = this.applicationName;
        
        if (result != null){
            // First process the ellipsis " ... "
            if (result.length() > MAX_APPLICATION_DISPLAY_SIZE) {
                int lastPathSeparatorIndex = result.lastIndexOf("\\");
                String applicationExecName = null;
                String applicationPath = null;
                if (lastPathSeparatorIndex > -1){
                    applicationExecName = result.substring(lastPathSeparatorIndex, result.length());
                    applicationPath = result.substring(0, lastPathSeparatorIndex);
                } else {
                    applicationExecName = result;
                    applicationPath = "";
                }
                if (result.length() > MAX_APPLICATION_DISPLAY_SIZE){ // we need to shorten the application
                    // shorten by the length of " ... ", which is 5 characters
                    int shortenByLength = 5;
                    if (applicationPath.length() > shortenByLength  &&
                        applicationExecName.length() < (MAX_APPLICATION_DISPLAY_SIZE - shortenByLength)){ 
                        // the path is long enough to be shorten and the application name is short enough 
                        // that it makes sense to shorten the path
                        result = applicationPath.substring(0, MAX_APPLICATION_DISPLAY_SIZE - applicationExecName.length() - shortenByLength) + 
                        " ... " + 
                        applicationExecName;
                    } else {
                        // either the path is not long enough to be shorten or the application name itself 
                        // is too long.  In this case, just display the application name
                        result = applicationExecName.length() > MAX_APPLICATION_DISPLAY_SIZE ? 
                                 applicationExecName.substring(0, MAX_APPLICATION_DISPLAY_SIZE - shortenByLength) + " ... " :
                                 applicationExecName;
                    }
                }
            }

            // Then insert a space for wrapping
            if (result.length() > (MAX_APPLICATION_DISPLAY_SIZE / 2)) {
				String processedPolicy = forHTMLTag(result.substring(0,	MAX_APPLICATION_DISPLAY_SIZE / 2))
						+ HTML_WBR
						+ forHTMLTag(result.substring(MAX_APPLICATION_DISPLAY_SIZE / 2, result.length()));
				result = processedPolicy;
			} else {
				result = forHTMLTag(result);
			}
        } else {
            result = "";
        }
        return result; 
    }
    
    /**
     *  @return an escaped string, may contain <wbr>
     */
    public String getApplicationPrintName(){
        String result = "";
        final String name = this.applicationName;
        if (name != null) {
            result = name;
            String processedApplication = "";
            for (int i = 0; i < 4; i++){
                String subString = result.substring(MAX_APPLICATION_DISPLAY_SIZE/2*i);
                if (subString.length() > MAX_APPLICATION_DISPLAY_SIZE/2){
                    processedApplication += forHTMLTag(result.substring(MAX_APPLICATION_DISPLAY_SIZE/2*i, MAX_APPLICATION_DISPLAY_SIZE/2*(i+1)));
                    processedApplication += HTML_WBR;
                } else {
                    processedApplication += forHTMLTag(subString);
                    break;
                }
            }
            result = processedApplication;
        }
        return result;
    }
    
    /**
     * Returns the shortened from resource name
     * 
     * @return the shortened from resource name, and the string is escaped and may contain <wbr>
     */
    public String getFromResourceShortened(){
        return this.fromResourceShortened;
    }

    
    /**
     * Remove the URL file scheme (file://) from the resource. Also ensure
     * localhost is added if necessary
     * 
     * @param resource
     * @return
     */
    private String processFileURLScheme(String resource) {
        if (resource == null) {
            throw new NullPointerException("resource cannot be null.");
        }

        if (resource.toLowerCase().startsWith(FILE_URL_SCHEMA_PREFIX)) {
            // -1 to leave the leading '/' character
            resource = resource.substring(FILE_URL_SCHEMA_PREFIX.length() - 1);
        }

        // Insert localhost if necessary
        if (resource.startsWith(DOUBLE_PATH_SEPARATOR)) {
            resource = resource.replaceFirst(DOUBLE_PATH_SEPARATOR, LOCALHOST_INSERTION);
        }

        return resource;
    }

    /**
     * Sets the action to be displayed
     * 
     * @param wsAction
     *            web service action type
     */
    private void setAction(String wsAction) {
        ActionEnumType actionEnum = ActionEnumType.getActionEnum(wsAction);
        UIActionType uiActionType = UIActionType.getUIActionType(actionEnum.getName());
        this.action = UIActionType.getDisplayValue(uiActionType);
    }

    /**
     * Sets the enforcement to be displayed
     * 
     * @param wsEffect
     *            web service enforcement type
     */
    private void setEnforcement(EffectType wsEffect) {
        String enforcementToSet = null;
        if (wsEffect != null) {
            PolicyDecisionEnumType enforcement = PolicyDecisionEnumType.getPolicyDecisionEnum(wsEffect.toString());
            UIEnforcementType uiEnforcement = UIEnforcementType.getUIEnforcementType(enforcement.getName());
            enforcementToSet = UIEnforcementType.getDisplayValue(uiEnforcement);
        }
        this.enforcement = enforcementToSet;
    }

    /**
     * Processes the "from resource" name to be displayed properly on the UI
     * 
     * @param rawFromResourceName
     *            initial value of the field
     */
    private void setFromResourceName(final String rawFromResourceName) {
    	//FIXME, get the NO_ATTACHMENT_STR from PolicyEvalutorImpl
    	if(rawFromResourceName.equals(NO_ATTACHMENT_STR)){
    	    fromResourceFilename = "";
    	    fromResourcePath = "";
    	    fromResource = "";
    	    fromResourceShortened = "";
    	    return;
    	}
    	
        //From resource name cannot be null
        this.fromResource = processFileURLScheme(rawFromResourceName);
        // this.fromResource = rawFromResourceName;
        int lastIndexOfPathSeparator = this.fromResource.lastIndexOf(PATH_SEPARATOR);
        //Is this appropriate? What happens on Unix?
        if (lastIndexOfPathSeparator != -1) {
            this.fromResourceFilename = this.fromResource.substring(lastIndexOfPathSeparator + 1);
            this.fromResourcePath = this.fromResource.substring(0, lastIndexOfPathSeparator);
        } else {
            lastIndexOfPathSeparator = this.fromResource.lastIndexOf(WINDOWS_PATH_SEPARATOR);
            if (lastIndexOfPathSeparator != -1) {
                this.fromResourceFilename = this.fromResource.substring(lastIndexOfPathSeparator + 1);
                this.fromResourcePath = this.fromResource.substring(0, lastIndexOfPathSeparator);
            } else {
                // I don't believe this can happen
                this.fromResourceFilename = this.fromResource;
                this.fromResourcePath = PATH_SEPARATOR;
            } 
        }
        
        // FIXME - 50 should be configurable
        // Now we need to set the fromResourceShortened property for displaying in the reporter
        if (this.fromResource.length() > MAX_RESOURCE_DISPLAY_SIZE) { // need to shorten the string            
            // This is the length we need shorten the resource path by
            // The number 12 is chosen because " ... /" is 6 characters and 
            // to give some buffer, the number is doubled so we get 12
            int shortenByLength = 12;
            if (this.fromResourcePath.length() > shortenByLength  &&
                this.fromResourceFilename.length() < (MAX_RESOURCE_DISPLAY_SIZE - shortenByLength)){ 
                // the path is long enough to be shorten and the filename is short enough that it makes
                // sense to shorten the path
                this.fromResourceShortened = this.fromResourcePath.substring(0, MAX_RESOURCE_DISPLAY_SIZE - this.fromResourceFilename.length() - shortenByLength) + 
                                             " ... /" + 
                                             this.fromResourceFilename;
            } else {
                // either the path is not long enough to be shorten or the filename itself is too long
                // in this case, just display the filename
                this.fromResourceShortened = 
                    this.fromResourceFilename.length() > MAX_RESOURCE_DISPLAY_SIZE ? 
                    this.fromResourceFilename.substring(0, MAX_RESOURCE_DISPLAY_SIZE - shortenByLength) + " ... " :
                    this.fromResourceFilename;
            }
            this.fromResourceShortened = forHTMLTag(this.fromResourceShortened);
        } else { // do not need to shorten the string
            if (this.fromResource.length() > MAX_RESOURCE_DISPLAY_SIZE/2 &&
                (this.fromResource.indexOf(" ") > MAX_RESOURCE_DISPLAY_SIZE/2 ||
                 this.fromResource.indexOf(" ") == -1)){
                String processedFromResource = forHTMLTag(this.fromResource.substring(0, MAX_RESOURCE_DISPLAY_SIZE/2)) +
                							   HTML_WBR +
                							   forHTMLTag(this.fromResource.substring(MAX_RESOURCE_DISPLAY_SIZE/2, this.fromResource.length()));
                this.fromResourceShortened = processedFromResource;
            } else {
                this.fromResourceShortened = forHTMLTag(this.fromResource);
            }
        }
    }

    /**
     * Extracts the policy name from the policy folder
     * 
     * @param rawPolicyName
     *            initial policy name
     */
    private void setPolicyName(final String rawPolicyName) {
        //Splits up the policy folder name from the policy name
        final String fullPolicyName = rawPolicyName;
        this.policyFolderName = "";
        this.policyName = "";
        if (fullPolicyName != null) {
            StringTokenizer st = new StringTokenizer(fullPolicyName, POLICY_FOLDER_SEPARATOR);
            int nbTokens = st.countTokens();
            for (int i = 0; i < nbTokens - 1; i++) {
                this.policyFolderName += POLICY_FOLDER_SEPARATOR + st.nextToken();
            }
            this.policyName = st.nextToken();
        }
    }

    /**
     * Processes the "to resource" name to be displayed properly on the UI
     * 
     * @param rawToResourceName
     *            initial value of the field
     */
    private void setToResourceName(final String rawToResourceName) {
        //To resource name may be null
        this.toResource = rawToResourceName;
        if ((this.toResource != null) && (!toResource.equals(""))) {
            this.toResource = processFileURLScheme(toResource);
            int lastIndexOfPathSeparator = this.toResource.lastIndexOf(PATH_SEPARATOR);
            if (lastIndexOfPathSeparator != -1) {
                this.toResourceFilename = this.toResource.substring(lastIndexOfPathSeparator + 1);
                this.toResourcePath = this.toResource.substring(0, lastIndexOfPathSeparator);
            } else {
                // I don't believe that this can happen
                this.toResourceFilename = this.toResource;
                this.toResourcePath = PATH_SEPARATOR;
            }
            // FIX ME - 50 should be configurable
            if (this.toResourcePath.length() > MAX_RESOURCE_DISPLAY_SIZE) {
                this.toResourcePath = this.toResourcePath.substring(0, MAX_RESOURCE_DISPLAY_SIZE - 4) + "...";
            }
        } else {
            this.toResourceFilename = "";
            this.toResourcePath = "";
        }
    }
    
    /**
     * Set the display value for the logging level
     *
     */
    private void setLoggingLevelDisplay(){
        if (this.loggingLevel == 1){
            this.loggingLevelDisplay = ReportLoggingLevelUIType.getDisplayValue(ReportLoggingLevelUIType.LOGGING_LEVEL_ONE);
        } else if (this.loggingLevel == 2){
            this.loggingLevelDisplay = ReportLoggingLevelUIType.getDisplayValue(ReportLoggingLevelUIType.LOGGING_LEVEL_TWO);
        } else if (this.loggingLevel == 3){
            this.loggingLevelDisplay = ReportLoggingLevelUIType.getDisplayValue(ReportLoggingLevelUIType.LOGGING_LEVEL_THREE);
        }
    }
    
    /**
     * Replace characters having special meaning <em>inside</em> HTML tags
     * with their escaped equivalents, using character entities such as <tt>'&amp;'</tt>.
     *
     * <P>The escaped characters are :
     * <ul>
     * <li> <
     * <li> >
     * <li> "
     * <li> '
     * <li> \
     * <li> &
     * </ul>
     *
     * <P>This method ensures that arbitrary text appearing inside a tag does not "confuse"
     * the tag. For example, <tt>HREF='Blah.do?Page=1&Sort=ASC'</tt>
     * does not comply with strict HTML because of the ampersand, and should be changed to
     * <tt>HREF='Blah.do?Page=1&amp;Sort=ASC'</tt>. This is commonly seen in building
     * query strings. (In JSTL, the c:url tag performs this task automatically.)
     */
     public static String forHTMLTag(String aTagFragment){
       final StringBuilder result = new StringBuilder();
       final StringCharacterIterator iterator = new StringCharacterIterator(aTagFragment);
       char character =  iterator.current();
       while (character != CharacterIterator.DONE ){
         if (character == '<') {
           result.append("&lt;");
         }
         else if (character == '>') {
           result.append("&gt;");
         }
         else if (character == '\"') {
           result.append("&quot;");
         }
         else if (character == '\'') {
           result.append("&#039;");
         }
         else if (character == '\\') {
            result.append("&#092;");
         }
         else if (character == '&') {
            result.append("&amp;");
         }
         else {
           //the char is not a special one
           //add it to the result as is
           result.append(character);
         }
         character = iterator.next();
       }
       return result.toString();
     }
     
     public static String forUnescapedHTML(String escapedString){
    	 escapedString = escapedString.replace("&lt;", "<");
    	 escapedString = escapedString.replace("&gt;", ">");
    	 escapedString = escapedString.replace("&quot;", "\"");
    	 escapedString = escapedString.replace("&#039;", "\'");
    	 escapedString = escapedString.replace("&#092;", "\\");
    	 escapedString = escapedString.replace("&amp;", "&");

         return escapedString;
       }
}
