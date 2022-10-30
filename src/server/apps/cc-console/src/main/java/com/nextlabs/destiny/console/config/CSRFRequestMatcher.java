package com.nextlabs.destiny.console.config;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static com.nextlabs.serverapps.common.framework.services.JwtValidationService.ID_TOKEN_HEADER;

/**
 * This matcher select requests to validate for CSRF protection.
 *
 * @author Sachindra Dasun
 */
public class CSRFRequestMatcher implements RequestMatcher {

    // This matcher can be removed once state changing GET requests are changed to POST.
    private AntPathRequestMatcher csrfCheckSkipMatcher = new AntPathRequestMatcher("/api/v1/system/csrfToken");
    private AntPathRequestMatcher apiRequestMatcher = new AntPathRequestMatcher("/api/**");
    private AntPathRequestMatcher scimApiRequestMatcher = new AntPathRequestMatcher("/scim/**");
    private AntPathRequestMatcher logoutRequestMatcher = new AntPathRequestMatcher("/logout");

    @Override
    public boolean matches(HttpServletRequest httpServletRequest) {

        // ID token is validated in JwtValidationFilter
        String idToken = httpServletRequest.getHeader(ID_TOKEN_HEADER);
        if (csrfCheckSkipMatcher.matches(httpServletRequest) || StringUtils.isNotBlank(idToken)) {
            return false;
        }
        return apiRequestMatcher.matches(httpServletRequest) || scimApiRequestMatcher.matches(httpServletRequest)
                || logoutRequestMatcher.matches(httpServletRequest);
    }
}
