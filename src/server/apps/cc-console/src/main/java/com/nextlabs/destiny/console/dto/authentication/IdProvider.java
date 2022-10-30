package com.nextlabs.destiny.console.dto.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

/**
 * Identity provider interface
 *
 * @author schok
 * @since 9.0
 */
public interface IdProvider {

    /**
     * Populate authentication handler information collected from UI to system configuration
     *
     * @param index Configuration index. Started from 0
     * @param authenticationConfigMapping Authentication handler configuration
     * @param userAttributeMapping User attribute mapping
     * @return Mapped system configuration from authentication handler details
     */
    Map<String, String> getSystemConfigMap(int index, Map<String, String> authenticationConfigMapping, String userAttributeMapping)
                    throws JsonProcessingException;

}
