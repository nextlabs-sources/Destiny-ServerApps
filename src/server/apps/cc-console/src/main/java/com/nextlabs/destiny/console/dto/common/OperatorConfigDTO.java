/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 30, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import com.nextlabs.destiny.console.model.policy.OperatorConfig;

/**
 *
 * DTO for Data Type and Operators Configuration, reference entity
 * {@link OperatorConfig}
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public class OperatorConfigDTO extends BaseDTO {

    private static final long serialVersionUID = 2162539420775554655L;

    private String key;
    private String label;
    private String dataType;

    /**
     * Transform {@link OperatorConfig} entity to DTO
     * 
     * @param OperatorConfig
     * @return {@link OperatorConfigDTO}
     */
    public static OperatorConfigDTO getDTO(OperatorConfig operatorConfig) {

        OperatorConfigDTO configDto = new OperatorConfigDTO();
        configDto.setId(operatorConfig.getId());
        configDto.setKey(operatorConfig.getKey());
        configDto.setLabel(operatorConfig.getLabel());
        configDto.setDataType(operatorConfig.getDataType().name());

        return configDto;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

}
