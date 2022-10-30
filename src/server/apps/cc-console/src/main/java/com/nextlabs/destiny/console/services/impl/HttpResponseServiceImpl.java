package com.nextlabs.destiny.console.services.impl;

import com.nextlabs.destiny.console.services.HttpResponseService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class HttpResponseServiceImpl implements HttpResponseService {

    private MessageBundleService msgBundle;

    @Autowired
    public HttpResponseServiceImpl(MessageBundleService msgBundle) {
        this.msgBundle = msgBundle;
    }

    public void respondUnAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        String code = msgBundle.getText("server.request.not.authenticated.code");
        String msg = msgBundle.getText("server.request.not.authenticated");
        String responseMsg = String.format("{\"statusCode\":\"%s\",\"message\":\"%s\"}", code, msg);
        response.getWriter().println(responseMsg);
        response.getWriter().flush();
    }
}
