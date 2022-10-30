/*
 *  All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

import java.sql.Timestamp;
import java.util.Map;

import com.nextlabs.destiny.inquirycenter.report.birt.InquiryCenterConfigPlugin;

/**
 * Holds the data from each record of the report tracking log table. This
 * is typically used to populate the Details Table Report.
 * @author ssen
 *
 */
public class DetailsTableData {

    private long id;
    private Timestamp time;
    private long monthNb;
    private long dayNb;
    private long hostId;
    private String hostIP;
    private String hostName;
    private long userId;
    private String userName;
    private String userSid;
    private long applicationId;
    private String applicationName;
    private String action;
    private int logLevel;
    private String fromResourceName;
    private Long fromResourceSize;
    private String fromResourceOwnerId;
    private long fromResourceCreatedDate;
    private long fromResourceModifiedDate;
    private String fromResourcePrefix;
    private String fromResourcePath;
    private String fromResourceShortName;
    private String toResourceName;

    // TODO - This needs to be invoked to initialize the class with the
    // action list - why does InquiryCenterConfigPlugin use arrays and not hashmap?
    public DetailsTableData () {
        InquiryCenterConfigPlugin.getInstance();
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public long getMontNb() {
        return monthNb;
    }

    public void setMonthNb(long monthNb) {
        this.monthNb = monthNb;
    }

    public long getDayNb() {
        return dayNb;
    }

    public void setDayNb(long dayNb) {
        this.dayNb = dayNb;
    }

    public long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public String getHostIP() {
        return hostIP;
    }

    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getAction() {
        return action;
    }

	public void setAction(String action) {
		Map<String, InquiryCenterAction> actionsMap = InquiryCenterConfigPlugin.getActions();
		String result = "Unknown";
		if (actionsMap != null) {
			InquiryCenterAction inqAction = actionsMap.get(action);
			if (inqAction != null) {
				result = inqAction.getLongName();
			}
		}
		if (action != null && result.equals("Unknown")) {
			this.action = action;
		} else {
			this.action = result;
		}
	}

    public String getFromResourceName() {
        return this.fromResourceName;
    }

    public void setFromResourceName(String fromResourceName) {
       /* if (fromResourceName.lastIndexOf("/", fromResourceName.length()) > 0) {
            fromResourceName = fromResourceName.substring(
                    fromResourceName.lastIndexOf("/", 
                            fromResourceName.length())+1, fromResourceName.length());
        }*/
        this.fromResourceName = fromResourceName;       
    }

    public long getFromResourceCreatedDate() {
        return fromResourceCreatedDate;
    }

    public void setFromResourceCreatedDate(long fromResourceCreatedDate) {
        this.fromResourceCreatedDate = fromResourceCreatedDate;
    }

    public long getFromResourceModifiedDate() {
        return fromResourceModifiedDate;
    }

    public void setFromResourceModifiedDate(long fromResourceModifiedDate) {
        this.fromResourceModifiedDate = fromResourceModifiedDate;
    }

    public String getToResourceName() {
        return toResourceName;
    }

    public void setToResourceName(String toResourceName) {
        this.toResourceName = toResourceName;
    }

    public String getUserSid() {
        return userSid;
    }

    public void setUserSid(String userSid) {
        this.userSid = userSid;
    }

    public Long getFromResourceSize() {
        return fromResourceSize;
    }

    public void setFromResourceSize(Long fromResourceSize) {
        this.fromResourceSize = fromResourceSize;
    }

    public String getFromResourceOwnerId() {
        return fromResourceOwnerId;
    }

    public void setFromResourceOwnerId(String fromResourceOwnerId) {
        this.fromResourceOwnerId = fromResourceOwnerId;
    }

    public String getFromResourcePrefix() {
        return fromResourcePrefix;
    }

    public void setFromResourcePrefix(String fromResourcePrefix) {
        this.fromResourcePrefix = fromResourcePrefix;
    }

    public String getFromResourcePath() {
        return fromResourcePath;
    }

    public void setFromResourcePath(String fromResourcePath) {
        this.fromResourcePath = fromResourcePath;
    }

    public String getFromResourceShortName() {
        return fromResourceShortName;
    }

    public void setFromResourceShortName(String fromResourceShortName) {
        this.fromResourceShortName = fromResourceShortName;
    }

    public long getMonthNb() {
        return monthNb;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

}
