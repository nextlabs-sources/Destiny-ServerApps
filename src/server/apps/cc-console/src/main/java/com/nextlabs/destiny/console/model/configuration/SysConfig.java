package com.nextlabs.destiny.console.model.configuration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.UpdateTimestamp;

import com.nextlabs.destiny.console.utils.SecurityContextUtil;

/**
 * Entity for system configuration.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "SYS_CONFIG")
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 8537463181561603109L;
    public static final String ATTRIBUTE_CONFIG_KEY = "configKey";
    public static final String ATTRIBUTE_MAIN_GROUP = "mainGroup";
    public static final String ATTRIBUTE_SUB_GROUP = "subGroup";
    public static final String ATTRIBUTE_DESCRIPTION = "description";
    public static final String ATTRIBUTE_HIDDEN = "hidden";
    public static final String ATTRIBUTE_ADVANCED = "advanced";
    public static final String TEXT = "text";

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "APPLICATION")
    private String application;

    @Column(name = "CONFIG_KEY")
    private String configKey;

    @Column(name = "VALUE", length = 4000)
    private String value;

    @Column(name = "DEFAULT_VALUE", length = 4000)
    private String defaultValue;

    @Column(name = "VALUE_FORMAT")
    private String valueFormat;

    @Column(name = "MAIN_GROUP")
    private String mainGroup;

    @Column(name = "SUB_GROUP")
    private String subGroup;

    @Column(name = "MAIN_GROUP_ORDER")
    private long mainGroupOrder;
    
    @Column(name = "SUB_GROUP_ORDER")
    private long subGroupOrder;
    
    @Column(name = "CONFIG_ORDER")
    private long configOrder;

    @Column(name = "HIDDEN")
    private boolean hidden;

    @Column(name = "READ_ONLY")
    private boolean readOnly;
    
    @Column(name = "ADVANCED")
    private boolean advanced;

    @Column(name = "UI")
    private boolean ui;

    @Column(name = "ENCRYPTED")
    private boolean encrypted;

    @Column(name = "RESTART_REQUIRED")
    private boolean restartRequired;

    @Column(name = "DATA_TYPE", length = 20)
    private String dataType;

    @Column(name = "FIELD_TYPE", length = 20)
    private String fieldType;

    @Column(name = "OPTIONS", length = 4000)
    private String options;

    @Column(name = "REQUIRED")
    private boolean required;

    @Column(name = "PATTERN", length = 4000)
    private String pattern;

    @Column(name = "DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "MODIFIED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modifiedOn;

    @Column(name = "MODIFIED_BY")
    private long modifiedBy;

    @PreUpdate
    public void preUpdate() {
        this.modifiedBy = SecurityContextUtil.getCurrentUser().getUserId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValueFormat() {
        return valueFormat;
    }

    public void setValueFormat(String valueFormat) {
        this.valueFormat = valueFormat;
    }

    public String getMainGroup() {
        return mainGroup;
    }

    public void setMainGroup(String mainGroup) {
        this.mainGroup = mainGroup;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(String subGroup) {
        this.subGroup = subGroup;
    }

    public long getMainGroupOrder() {
		return mainGroupOrder;
	}

	public void setMainGroupOrder(long mainGroupOrder) {
		this.mainGroupOrder = mainGroupOrder;
	}

	public long getSubGroupOrder() {
		return subGroupOrder;
	}

	public void setSubGroupOrder(long subGroupOrder) {
		this.subGroupOrder = subGroupOrder;
	}

	public long getConfigOrder() {
        return configOrder;
    }

    public void setConfigOrder(long configOrder) {
        this.configOrder = configOrder;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isAdvanced() {
		return advanced;
	}

	public void setAdvanced(boolean advanced) {
		this.advanced = advanced;
	}

	public boolean isUi() {
        return ui;
    }

    public void setUi(boolean ui) {
        this.ui = ui;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public boolean isRestartRequired() {
        return restartRequired;
    }

    public void setRestartRequired(boolean restartRequired) {
        this.restartRequired = restartRequired;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

}
