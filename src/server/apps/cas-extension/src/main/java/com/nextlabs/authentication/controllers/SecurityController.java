package com.nextlabs.authentication.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.authentication.filters.CsrfTokenGeneratingFilter;

/**
 * This controller can be accessed to get CSRF token header.
 *
 * @author Sachindra Dasun
 */
@RestController
@RequestMapping("security")
public class SecurityController {

    @GetMapping(value = "csrfToken")
    public ResponseEntity<String> getCsrfToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession != null) {
            Object csrfTokenObject = httpSession.getAttribute(CsrfTokenGeneratingFilter.CSRF_TOKEN_ATTR);
            if (csrfTokenObject != null) {
                httpServletResponse.setHeader("X-CSRF-HEADER", CsrfTokenGeneratingFilter.CSRF_TOKEN_HEADER);
                httpServletResponse.setHeader(CsrfTokenGeneratingFilter.CSRF_TOKEN_HEADER, csrfTokenObject.toString());
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
