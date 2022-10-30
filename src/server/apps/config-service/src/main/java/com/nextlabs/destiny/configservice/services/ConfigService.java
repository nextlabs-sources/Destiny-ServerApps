package com.nextlabs.destiny.configservice.services;

import java.util.List;

import com.nextlabs.destiny.configservice.dto.SysConfigValueDTO;

/**
 * Configuration service.
 *
 * @author Sachindra Dasun
 */
public interface ConfigService {

    void update(List<SysConfigValueDTO> sysConfigValueDTOS);

    void reset(List<SysConfigValueDTO> sysConfigValueDTOS);
}
