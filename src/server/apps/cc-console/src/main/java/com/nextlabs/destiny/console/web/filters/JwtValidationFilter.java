package com.nextlabs.destiny.console.web.filters;

import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.enums.LogMarker;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.services.ApplicationUserService;
import com.nextlabs.destiny.console.services.HttpResponseService;
import com.nextlabs.serverapps.common.framework.services.JwtValidationService;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter validates Json Web Tokens passed in the header.
 *
 * @author Mohammed Sainal Shah
 */

public class JwtValidationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtValidationFilter.class);

    private JwtValidationService jwtValidationService;

    private ApplicationUserService applicationUserService;

    private HttpResponseService httpResponseService;

    public JwtValidationFilter(JwtValidationService jwtValidationService, ApplicationUserService applicationUserService,
                               HttpResponseService httpResponseService) {
        this.jwtValidationService = jwtValidationService;
        this.applicationUserService = applicationUserService;
        this.httpResponseService = httpResponseService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String idToken = request.getHeader(JwtValidationService.ID_TOKEN_HEADER);
        if (StringUtils.isNotBlank(idToken)) {
            idToken = stripBearer(idToken);
            JwtClaims jwtClaims = jwtValidationService.validateJwt(idToken);
            if (jwtClaims != null) {
                try {
                    ApplicationUser appUser = applicationUserService.findByUsernamePopulateDelegationPolicy(jwtClaims.getSubject());
                    if (appUser != null) {
                        logger.info("JWT claims validated");
                        PrincipalUser principal = new PrincipalUser(appUser.getId(), appUser.getFirstName(), appUser.getLastName(),
                                appUser.getDisplayName(), appUser.getUsername(), "*******", appUser.getUserType(), appUser.getUserCategory(),
                                appUser.isSuperUser(), appUser.isHideSplash(), applicationUserService.getAllAuthorities(appUser));
                        Authentication auth = new UsernamePasswordAuthenticationToken(principal,
                                null, applicationUserService.getAllAuthorities(appUser));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        filterChain.doFilter(request, response);
                        return;
                    }
                } catch (MalformedClaimException | ConsoleException e) {
                    logger.warn("JWT claims validation failed");
                }
            }
            logger.warn(LogMarker.SECURITY, "Not authenticated access. [resource=[method={}, url={}], remoteHost={}]",
                    request.getMethod(), request.getRequestURI(), request.getRemoteHost());
            httpResponseService.respondUnAuthenticated(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String stripBearer(String idToken){
        return idToken.replace(JwtValidationService.BEARER_PREFIX, "");
    }
}
