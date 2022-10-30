package com.nextlabs.destiny.console.services;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

import org.xml.sax.SAXException;

import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.config.LoggerConfigDTO;
import com.nextlabs.destiny.console.dto.config.LoggerConfigValueDTO;

/**
 * Service to obtain and update logger configurations.
 *
 * @author Sachindra Dasun
 */
public interface LoggerConfigService {

    List<LoggerConfigDTO> findAll();

    String getCustomConfig();

    void save(String config);

    void updateLoggers(List<LoggerConfigValueDTO> loggerConfigValueDTOS) throws ParserConfigurationException,
            IOException, SAXException, TransformerException;

    List<LoggerConfigDTO> search(SearchCriteria searchCriteria);

    boolean isValid(String config);

}
