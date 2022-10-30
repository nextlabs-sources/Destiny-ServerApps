package com.nextlabs.destiny.console.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.AgentDTO;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.services.AgentSearchService;

/**
 * @author Sachindra Dasun
 */
@RestController
@ApiVersion(1)
@RequestMapping("/agent")
public class AgentController extends AbstractRestController {
    private static final Logger log = LoggerFactory.getLogger(AgentController.class);
    @Autowired
    private AgentSearchService agentSearchService;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find")
    public ConsoleResponseEntity<CollectionDataResponseDTO> find(@RequestParam("type") List<String> types,
                                                                 @RequestParam String value) {
        validations.assertCollectionEmpty(types, "type");
        log.debug("Request came to load agents");

        List<AgentDTO> agents = agentSearchService.find(types, value).stream()
                .map(AgentDTO::getDTO)
                .collect(Collectors.toList());

        CollectionDataResponseDTO<AgentDTO> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText("success.data.found.code"),
                msgBundle.getText("success.data.found"));
        response.setData(agents);
        response.setPageSize(agents.size());
        response.setPageNo(0);
        response.setTotalPages(1);
        response.setTotalNoOfRecords(agents.size());

        log.info("Requested agents found and response sent");
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return log;
    }
}
