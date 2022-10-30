package com.nextlabs.authentication.filters;

import com.nextlabs.serverapps.common.properties.CasOidcProperties;
import com.nextlabs.serverapps.common.properties.CCOIDCService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Populates registered client id and client
 * secret to the request to CAS OIDC access token request.
 *
 * @author Mohammed Sainal Shah
 */

@Component
public class OIDCClientEnumerateFilter extends OncePerRequestFilter {

    private CasOidcProperties casOidcProperties;

    private static final String CLIENT_ID_KEY = "client_id";
    private static final String CLIENT_SECRET_KEY = "client_secret";

    @Autowired
    public OIDCClientEnumerateFilter(CasOidcProperties casOidcProperties) {
        this.casOidcProperties = casOidcProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(enumerateClientSecret(request), response);
    }

    private HttpServletRequest enumerateClientSecret(final HttpServletRequest req) {
        final Map<String, String[]> adjustedParams = populateClientConfig(req);
        return new HttpServletRequestWrapper(req) {
            public String getParameter(String name) {
                return adjustedParams.get(name) == null ? null : adjustedParams.get(name)[0];
            }

            public Map<String, String[]> getParameterMap() {
                return adjustedParams;
            }

            public Enumeration<String> getParameterNames() {
                return Collections.enumeration(adjustedParams.keySet());
            }

            public String[] getParameterValues(String name) {
                return adjustedParams.get(name);
            }
        };
    }

    // populate client secret only if it's not present in the request.
    private Map<String, String[]> populateClientConfig(HttpServletRequest req) {
        String clientId = req.getParameter(CLIENT_ID_KEY);
        String clientSecret = req.getParameter(CLIENT_SECRET_KEY);
        CCOIDCService ccOidcService = casOidcProperties.getOidcService(clientId);
        if(ccOidcService == null || StringUtils.isNotBlank(clientSecret)){
            return req.getParameterMap();
        }
        HashMap<String, String[]> newMap = new HashMap<>(req.getParameterMap());
        newMap.put(CLIENT_SECRET_KEY, new String[] {ccOidcService.getClientSecret()});
        return newMap;
    }
}
