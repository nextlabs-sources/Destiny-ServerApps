package com.nextlabs.destiny.inquirycenter.web.security;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet can be accessed to get CSRF token header.
 *
 * @author Sachindra Dasun
 */
public class CsrfTokenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
