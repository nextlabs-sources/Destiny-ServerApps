/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 22, 2016
 *
 */
package com.nextlabs.destiny.console.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * Application User Service to manage the application users
 *
 * @author Mohammed Sainal Shah
 *
 */
public interface HttpResponseService {

    /**
     * Writes Http Unauthenticated error to response.
     *
     * @param request
     *        request
     * @param response
     *        response
     *
     * @throws IOException
     */
    void respondUnAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
