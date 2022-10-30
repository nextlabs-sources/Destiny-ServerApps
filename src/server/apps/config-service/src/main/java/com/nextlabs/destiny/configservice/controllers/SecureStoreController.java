package com.nextlabs.destiny.configservice.controllers;

import com.nextlabs.destiny.configservice.services.MessageService;
import com.nextlabs.destiny.configservice.services.SecureStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * REST controller for secure store file management.
 *
 * @author Chok Shah Neng
 */
@RestController
@RequestMapping("secure-store")
public class SecureStoreController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureStoreController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private SecureStoreService secureStoreService;

    @GetMapping(value = "refresh")
    public ResponseEntity<String> refresh(@RequestParam(name = "applications", required = false)
                    Set<String> applications) {
        messageService.sendSecureStoreRefresh(applications);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "download")
    public ResponseEntity download(HttpServletResponse response) {
        try {
            response.getOutputStream().write(secureStoreService.getStoreZip());
            response.getOutputStream().flush();
        } catch(IOException err) {
            LOGGER.error(err.getMessage(), err);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
