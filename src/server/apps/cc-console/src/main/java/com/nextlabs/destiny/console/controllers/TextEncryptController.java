package com.nextlabs.destiny.console.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.utils.SystemCodes;

/**
 * Text encrypt controller.
 *
 * @author Sachindra Dasun
 */
@RestController
@ApiVersion(1)
@RequestMapping("encrypt")
public class TextEncryptController extends AbstractRestController {

    private static final Logger logger = LoggerFactory.getLogger(TextEncryptController.class);

    @Autowired
    private TextEncryptor textEncryptor;

    @Override
    protected Logger getLog() {
        return logger;
    }

    @ApiIgnore
    @ResponseBody
    @PostMapping
    public ConsoleResponseEntity<SimpleResponseDTO<String>> encrypt(@RequestBody String text) {
        String encryptedText = textEncryptor.encrypt(text);
        return ConsoleResponseEntity.get(
                SimpleResponseDTO.createWithType(SystemCodes.DATA_FOUND_SUCCESS.getCode(),
                        SystemCodes.DATA_FOUND_SUCCESS.getMessageKey(), encryptedText), HttpStatus.OK);
    }

}

