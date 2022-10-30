/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 9, 2015
 *
 */
package com.nextlabs.destiny.console.dto.common;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * Base DTO
 *
 * @author Amila Sivla
 * @since 8.0
 *
 */
public abstract class BaseDTO implements Serializable {

    private static final long serialVersionUID = 5305224384722767670L;

    @NotBlank(groups = {GetMapping.class, PutMapping.class, DeleteMapping.class})
    protected Long id;

    @ApiModelProperty(example = "87")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
