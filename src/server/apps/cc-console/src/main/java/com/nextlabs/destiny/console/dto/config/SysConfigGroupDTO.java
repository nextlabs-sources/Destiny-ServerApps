package com.nextlabs.destiny.console.dto.config;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for system configuration group.
 *
 * @author Sachindra Dasun
 */
public class SysConfigGroupDTO implements Serializable {

    private static final long serialVersionUID = -4378422225226171024L;
    private String group;
    private long groupOrder;
    private Date lastModifiedOn;
    private long lastModifiedBy;
    private String lastModifiedByName;

    public SysConfigGroupDTO() {
    }

    public SysConfigGroupDTO(String group, long groupOrder) {
        this.group = group;
        this.groupOrder = groupOrder;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public long getGroupOrder() {
		return groupOrder;
	}

	public void setGroupOrder(long groupOrder) {
		this.groupOrder = groupOrder;
	}

	public Date getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedByName() {
        return lastModifiedByName;
    }

    public void setLastModifiedByName(String lastModifiedByName) {
        this.lastModifiedByName = lastModifiedByName;
    }

}
