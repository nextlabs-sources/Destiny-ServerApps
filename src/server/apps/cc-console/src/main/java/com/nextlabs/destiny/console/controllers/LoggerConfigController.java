package com.nextlabs.destiny.console.controllers;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.config.LoggerConfigDTO;
import com.nextlabs.destiny.console.dto.config.LoggerConfigValueDTO;
import com.nextlabs.destiny.console.dto.policymgmt.SearchCriteriaDTO;
import com.nextlabs.destiny.console.services.LoggerConfigService;

/**
 * REST controller to obtain and update logger configurations.
 *
 * @author Sachindra Dasun
 */
@RestController
@ApiVersion(1)
@RequestMapping("loggers")
public class LoggerConfigController extends AbstractRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerConfigController.class);

    @Autowired
    private LoggerConfigService loggerConfigService;

    @GetMapping("findAll")
    public ConsoleResponseEntity<List<LoggerConfigDTO>> findAll() {
        return ConsoleResponseEntity.get(loggerConfigService.findAll(), HttpStatus.OK);
    }

    @PostMapping("search")
    public ConsoleResponseEntity<List<LoggerConfigDTO>> search(@RequestBody SearchCriteriaDTO searchCriteriaDTO) {
        validations.assertNotNull(searchCriteriaDTO, "searchCriteriaDTO");
        List<LoggerConfigDTO> sysConfigGroupDTOS = loggerConfigService.search(searchCriteriaDTO.getCriteria());
        return ConsoleResponseEntity.get(sysConfigGroupDTOS, HttpStatus.OK);
    }

    @GetMapping("config")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> config() {
        SimpleResponseDTO<String> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), loggerConfigService.getCustomConfig());
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @PostMapping("save")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> save(@RequestBody(required = false) String config) {
        SimpleResponseDTO<String> response;
        if (loggerConfigService.isValid(config)) {
            loggerConfigService.save(config);
            response = SimpleResponseDTO.createWithType(
                    msgBundle.getText("success.data.saved.code"),
                    msgBundle.getText("success.data.saved"), msgBundle.getText("success.data.saved"));
        } else {
            response = SimpleResponseDTO.createWithType(
                    msgBundle.getText("invalid.logging.config.xml.code"),
                    msgBundle.getText("invalid.logging.config.xml.message"),
                    msgBundle.getText("invalid.logging.config.xml.message"));
        }
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @PostMapping("updateLoggers")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> updateLoggers(
            @RequestBody List<LoggerConfigValueDTO> loggerConfigValueDTOS)
            throws ParserConfigurationException, TransformerException, SAXException, IOException {
        loggerConfigService.updateLoggers(loggerConfigValueDTOS);
        SimpleResponseDTO<String> response = SimpleResponseDTO.createWithType(
                msgBundle.getText("success.data.saved.code"),
                msgBundle.getText("success.data.saved"), msgBundle.getText("success.data.saved"));
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return LOGGER;
    }
}
