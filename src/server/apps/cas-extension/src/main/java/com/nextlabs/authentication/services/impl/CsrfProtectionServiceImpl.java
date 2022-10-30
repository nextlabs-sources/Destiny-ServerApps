package com.nextlabs.authentication.services.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nextlabs.authentication.filters.CsrfTokenGeneratingFilter;
import com.nextlabs.authentication.services.CsrfProtectionService;

/**
 * CSRF protection service implementation.
 *
 * @author Sachindra Dasun
 */
@Service("csrfProtectionService")
public class CsrfProtectionServiceImpl implements CsrfProtectionService {

    @Override
    public String getCsrfToken() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        String csrfToken = null;
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest httpRequest = servletRequestAttributes.getRequest();
            HttpSession httpSession = httpRequest.getSession();
            Object csrfTokenObject = httpSession.getAttribute(CsrfTokenGeneratingFilter.CSRF_TOKEN_ATTR);
            if (csrfTokenObject == null || StringUtils.isEmpty(csrfTokenObject.toString())) {
                csrfToken = UUID.randomUUID().toString();
                httpSession.setAttribute(CsrfTokenGeneratingFilter.CSRF_TOKEN_ATTR, csrfToken);
            } else {
                csrfToken = csrfTokenObject.toString();
            }
        }
        return csrfToken;
    }

}
