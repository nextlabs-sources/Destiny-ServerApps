package com.nextlabs.destiny.configservice.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.configservice.dto.SysConfigValueDTO;
import com.nextlabs.destiny.configservice.services.ConfigService;

/**
 * Configuration Service implementation.
 *
 * @author Sachindra Dasun
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    private static final String UPDATE_SQL = "UPDATE SYS_CONFIG SET VALUE = ? WHERE APPLICATION = ? AND CONFIG_KEY = ?";
    private static final String RESET_SQL = "UPDATE SYS_CONFIG SET VALUE = DEFAULT_VALUE WHERE APPLICATION = ? AND CONFIG_KEY = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void update(List<SysConfigValueDTO> sysConfigValueDTOS) {
        jdbcTemplate.batchUpdate(UPDATE_SQL, sysConfigValueDTOS,
                sysConfigValueDTOS.size(), (ps, sysConfigValueDTO) -> {
                    ps.setString(1, sysConfigValueDTO.getValue());
                    ps.setString(2, sysConfigValueDTO.getApplication());
                    ps.setString(3, sysConfigValueDTO.getConfigKey());
                });
    }

    @Override
    public void reset(List<SysConfigValueDTO> sysConfigValueDTOS) {
        jdbcTemplate.batchUpdate(RESET_SQL, sysConfigValueDTOS,
                sysConfigValueDTOS.size(), (ps, sysConfigValueDTO) -> {
                    ps.setString(1, sysConfigValueDTO.getApplication());
                    ps.setString(2, sysConfigValueDTO.getConfigKey());
                });
    }
}
