/*
 * Created on Mar 7, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.defaultimpl;

import java.util.ArrayList;
import java.util.List;

import com.bluejungle.destiny.inquirycenter.report.defaultimpl.ReportDetailResultImpl;
import com.bluejungle.destiny.types.report_result.v1.DocumentActivityCustomResult;
import com.bluejungle.destiny.types.report_result.v1.PolicyActivityCustomResult;
import com.nextlabs.destiny.types.custom_attr.v1.CustomAttribute;
import com.nextlabs.destiny.types.custom_attr.v1.CustomAttributeList;

/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/report/defaultimpl/LogDetailResultImpl.java#1 $
 */

public class LogDetailResultImpl extends ReportDetailResultImpl {

    private List<CustomAttribute> customAttributes;
    
    public LogDetailResultImpl(PolicyActivityCustomResult log){
        super(log);
        setCustomAttributes(log.getCustomAttributeList());
    }
    
    public LogDetailResultImpl(DocumentActivityCustomResult log){
        super(log);
        setCustomAttributes(log.getCustomAttributeList());
    }
    
    public void setCustomAttributes(CustomAttributeList wsCustomAttributes){
        this.customAttributes = new ArrayList<CustomAttribute>();
        CustomAttribute[] wsCustomAttrArray = wsCustomAttributes.getCustomAttributes();
        
        /**
    	 * we can't guarantee the "Name" is the first one
    	 * we may have a trouble if we want to remove the value before the "name"
    	 * that's why we need to go thru the loop once first
    	 */
        boolean isNoAttachment = false;
        for (int i = 0; i < wsCustomAttrArray.length; i++) {
        	//FIXME, the keyword "Name" should not be hardcoded
        	String key = wsCustomAttrArray[i].getKey();
        	String value = wsCustomAttrArray[i].getValue();
			if (key.equalsIgnoreCase("Name")
					&& value != null
					&& value.equalsIgnoreCase(NO_ATTACHMENT_STR)) {
				isNoAttachment = true;
				break;
			}
		}
        
        for (int i = 0; i < wsCustomAttrArray.length; i++){
            /* HACK - Robert 09/24/2007 for Bug 5584
             * This logic is put in due to the fact that obligations can be multi-valued.
             * However, the 2.5 enforcer still uses the old serialization logic when 
             * encoding a log to be sent over to the server (dabs), therefore the dynamic 
             * type of the attribute is always string instead of MultiValue, which is the
             * original type before encoding the log.  
             * 
             * For now, we'll just check for the prefix "string:[" to identify multi-
             * valued attributes, this needs to be dealt with in 3.0
             */  
        	
        	/**
        	 * bugfix 5665
        	 * if the name is no attachment, remove "size", "name" "and modified date"
        	 */
        	if (isNoAttachment) {
				String key = wsCustomAttrArray[i].getKey();
				//FIXME, the keywords should not be hardcoded
				if (key.equalsIgnoreCase("Name") 
						|| key.equalsIgnoreCase("Size")
						|| key.equalsIgnoreCase("Modified Date")) {

                    CustomAttribute customAttribute = new CustomAttribute();
                    customAttribute.setKey(key);
				    customAttribute.setValue("");

					this.customAttributes.add(customAttribute);
					continue;
				}
			}
        	
        	String value = wsCustomAttrArray[i].getValue();
            if (value != null && value.startsWith("string:[")){
                value = value.substring(8);
                String subValue = value;
                while (true){
                    int separatorIndex = value.indexOf(",");
                    if (separatorIndex > -1){
                        subValue = value.substring(0, separatorIndex);
                        value = value.substring(separatorIndex+2);

                        CustomAttribute customAttribute = new CustomAttribute();
                        customAttribute.setKey(wsCustomAttrArray[i].getKey());
                        customAttribute.setValue(subValue);
                        this.customAttributes.add(customAttribute);
                    } else {
                        value = value.substring(0, value.length()-1);
                        CustomAttribute customAttribute = new CustomAttribute();
                        customAttribute.setKey(wsCustomAttrArray[i].getKey());
                        customAttribute.setValue(value);
                        this.customAttributes.add(customAttribute);
                        break;
                    }
                }
            } else {
                this.customAttributes.add(wsCustomAttrArray[i]);
            }
        }
    }

    
    /**
     * Returns the customAttributes.
     * @return the customAttributes.
     */
    public List<CustomAttribute> getCustomAttributes() {
        return this.customAttributes;
    }
    
    /**
     * Sets the customAttributes
     * @param customAttributes The customAttributes to set.
     */
    public void setCustomAttributes(ArrayList<CustomAttribute> customAttributes) {
        this.customAttributes = customAttributes;
    }   
}
