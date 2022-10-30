package com.nextlabs.destiny.console.advices;

import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.enums.LogMarker;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.utils.SecurityContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handler for Console exceptions.
 *
 * @author Mohammed Sainal Shah
 */
@ControllerAdvice
public class CCExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CCExceptionHandler.class);

    private final MessageBundleService msgBundle;

    public CCExceptionHandler(MessageBundleService msgBundle) {
        this.msgBundle = msgBundle;
    }

    @ExceptionHandler({ConsoleException.class})
    public ConsoleResponseEntity<ResponseDTO> errorHandler(ConsoleException e, WebRequest request) {
        log.error(e.getMessage(), e);
        log.error(LogMarker.SYSTEM, "Exception encountered. [message={}, userInfo={}]",
                e.getMessage(), SecurityContextUtil.getUserInfo());
        return ConsoleResponseEntity.get(
                ResponseDTO.create(msgBundle.getText("general.error.code"), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ServerException.class})
    protected ResponseEntity<Object> errorHandler(ServerException e, WebRequest request) {
        log.error(e.getMessage(), e);
        log.error(LogMarker.SYSTEM, "Exception encountered. [message={}, userInfo={}]",
                e.getMessage(), SecurityContextUtil.getUserInfo());
        return ConsoleResponseEntity.get(
                ResponseDTO.create(msgBundle.getText("general.error.code"), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({Exception.class})
    public ConsoleResponseEntity<ResponseDTO> errorHandler(Exception e, WebRequest request) {
        log.error(e.getMessage(), e);
        log.error(LogMarker.SYSTEM, "Exception encountered. [message={}, userInfo={}]",
                e.getMessage(), SecurityContextUtil.getUserInfo());
        return ConsoleResponseEntity.get(
                ResponseDTO.create(msgBundle.getText("general.error.code"), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
