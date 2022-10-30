package com.nextlabs.destiny.console.dto.config;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.nextlabs.destiny.console.dto.Auditable;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.configuration.SysConfig;
import com.nextlabs.destiny.console.utils.JsonUtil;

/**
 * DTO for system configuration.
 *
 * @author Sachindra Dasun
 */
public class SysConfigDTO implements Serializable, Auditable  {

    private static final long serialVersionUID = 1576481912547388390L;
    private long id;
    private String application;
    private String configKey;
    private String value;
    private String defaultValue;
    private String valueFormat;
    private String mainGroup;
    private String subGroup;
    private long mainGroupOrder;
    private long subGroupOrder;
    private long configOrder;
    private boolean hidden;
    private boolean readOnly;
    private boolean advanced;
    private boolean ui;
    private boolean encrypted;
    private boolean restartRequired;
    private String dataType;
    private String fieldType;
    private String options;
    private boolean required;
    private String pattern;
    private String description;
    private Date modifiedOn;
    private long modifiedBy;
    private String modifiedByName;
    private boolean valueEmpty;

    public SysConfigDTO() {
    }

    public SysConfigDTO(SysConfig sysConfig) {
        this.id = sysConfig.getId();
        this.application = sysConfig.getApplication();
        this.configKey = sysConfig.getConfigKey();
        this.value = sysConfig.getValue();
        this.defaultValue = sysConfig.getDefaultValue();
        this.valueFormat = sysConfig.getValueFormat();
        this.mainGroup = sysConfig.getMainGroup();
        this.subGroup = sysConfig.getSubGroup();
        this.mainGroupOrder = sysConfig.getMainGroupOrder();
        this.subGroupOrder = sysConfig.getSubGroupOrder();
        this.configOrder = sysConfig.getConfigOrder();
        this.hidden = sysConfig.isHidden();
        this.readOnly = sysConfig.isReadOnly();
        this.advanced = sysConfig.isAdvanced();
        this.ui = sysConfig.isUi();
        this.encrypted = sysConfig.isEncrypted();
        this.restartRequired = sysConfig.isRestartRequired();
        this.dataType = sysConfig.getDataType();
        this.fieldType = sysConfig.getFieldType();
        this.options = sysConfig.getOptions();
        this.required = sysConfig.isRequired();
        this.pattern = sysConfig.getPattern();
        this.description = sysConfig.getDescription();
        this.modifiedOn = sysConfig.getModifiedOn();
        this.modifiedBy = sysConfig.getModifiedBy();
    }

	public static SysConfigDTO getDTO(SysConfig sysConfig) {

		SysConfigDTO sysConfigDTO = new SysConfigDTO();
        sysConfigDTO.setId(sysConfig.getId());
        sysConfigDTO.setApplication(sysConfig.getApplication());
        sysConfigDTO.setMainGroup(sysConfig.getMainGroup());
        sysConfigDTO.setSubGroup(sysConfig.getSubGroup());
        sysConfigDTO.setConfigKey(sysConfig.getConfigKey());
        sysConfigDTO.setValue(sysConfig.getValue());
        sysConfigDTO.setDefaultValue(sysConfig.getDefaultValue());
        sysConfigDTO.setModifiedBy(sysConfig.getModifiedBy());
        sysConfigDTO.setModifiedOn(sysConfig.getModifiedOn());
        return sysConfigDTO;
    }

	@Override
	public String toAuditString() throws ConsoleException {
		try {
	    	Map<String, Object> audit = new LinkedHashMap<>();
	    	
	    	audit.put("Id", this.id);
	    	audit.put("Main Group", this.mainGroup);
	    	audit.put("Sub Group", this.subGroup);
	    	audit.put("Application", this.application);
	    	audit.put("Config Key", this.configKey);
	    	audit.put("Value", this.value);
	    	audit.put("Default Value", this.defaultValue);
	    	
	    	
	    	return JsonUtil.toJsonString(audit);
    	} catch(Exception e) {
    		throw new ConsoleException(e);
    	}
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

    public String getModifiedByName() {
        return modifiedByName;
    }

    public void setModifiedByName(String modifiedByName) {
        this.modifiedByName = modifiedByName;
    }

	public boolean isValueEmpty() {
		return valueEmpty;
	}

	public void setValueEmpty(boolean valueEmpty) {
		this.valueEmpty = valueEmpty;
	}
}
