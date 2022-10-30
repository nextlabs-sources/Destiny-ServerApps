/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 14, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nextlabs.destiny.console.dto.policymgmt.ExportEntityDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyExportOptionsDTO;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyPortingDTO;
import com.nextlabs.destiny.console.enums.ImportMechanism;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;

/**
 *
 * Service to handle all export and import of policy
 * 
 * This service will export all the related Policy models, components and its
 * sub components and sub policies
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface PolicyPortingService {

    /**
     * Export the given policies and its components, sub-components and policy
     * models and returns the exported file's filename
     * 
     * @param exportEntityDTOS
     * @param exportMode
     * @return
     * @throws ConsoleException
     */
    String exportAsFile(List<ExportEntityDTO> exportEntityDTOS, String exportMode) throws ConsoleException;

    /**
     * Export all the policies and its components, sub-components and policy
     * models
     * 
     * @param policyLites
     * @param exportMode
     * @return
     * @throws ConsoleException
     */
    String exportAll(List<PolicyLite> policyLites, String exportMode) throws ConsoleException;

    /**
     * Validate the given file and import the policy data.
     *
     * @param policyLites
     * @param exportMode
     * @return
     * @throws ConsoleException
     * @throws CircularReferenceException
     */
    byte[] validateAndExport(List<PolicyLite> policyLites, String exportMode) throws ConsoleException, JsonProcessingException;

    /**
     * Validate the given file and import the policy data.
     * 
     * @param bytes
     * @return
     * @throws ConsoleException
     * @throws CircularReferenceException
     */
    PolicyPortingDTO validateAndImport(byte[] bytes) throws ConsoleException, CircularReferenceException;

    /**
     * Validate the given file and import the policy data.
     * 
     * @param bytes
     * @param importMode
     * @return
     * @throws ConsoleException
     * @throws CircularReferenceException
     */
    PolicyPortingDTO validateAndImport(byte[] bytes, String importMode) throws ConsoleException, CircularReferenceException;

    /**
     * Validate the given file and import the policy data.
     *
     * @param bytes
     * @param importMode
     * @param importMechanism
     * @return
     * @throws ConsoleException
     * @throws CircularReferenceException
     */
    PolicyPortingDTO validateAndImport(byte[] bytes, String importMode, ImportMechanism importMechanism) throws ConsoleException, CircularReferenceException;

    /**
     * Perform cleanup for non-importing entities
     *
     * @param policyPortingDTO
     * @throws ConsoleException
     */
    void cleanup(PolicyPortingDTO policyPortingDTO) throws ConsoleException;

    /**
     * Validate the given file and import the policy data.
     *
     * @param exportEntityDTOS
     * @return PolicyPortingDTO
     * @throws ConsoleException
     * @throws CircularReferenceException
     */
    PolicyPortingDTO prepareDataToExport(List<ExportEntityDTO> exportEntityDTOS) throws ConsoleException;
    
    /**
     * Get policy export options
     * 
     * @return {@link PolicyExportOptionsDTO}
     * @throws ConsoleException
     */
    PolicyExportOptionsDTO getExportOptions() throws ConsoleException;

    /**
     * Get File extension based on exportMode
     * @param exportMode
     * @return {@link String}
     */
    String getFileExtension(String exportMode);

    /**
     * Generate PDF for all the given policies. Contains the policies in natural language
     *
     * @param exportEntityDTOS
     * @throws ConsoleException
     * @throws ServerException
     */
    String generatePDF(List<ExportEntityDTO> exportEntityDTOS)
            throws ConsoleException, ServerException;

    /**
     * Export the given xacml policies
     *
     * @param ids
     * @return
     * @throws ConsoleException
     */
    List<String> exportXacmlPolicy(List<Long> ids) throws ConsoleException;

    String generateXACML(List<ExportEntityDTO> exportEntityDTOS)
            throws ConsoleException, ServerException;

}
