package com.nextlabs.destiny.console.config.listeners;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.model.RevInfo;

/**
 * This listener is adding extra details to revision information.
 *
 * @author Sachindra Dasun
 */
public class RevInfoListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        RevInfo revInfo = (RevInfo) revisionEntity;
        addUserDetails(revInfo);
    }

    private void addUserDetails(RevInfo revInfo) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof PrincipalUser) {
                    PrincipalUser principalUser = (PrincipalUser) principal;
                    revInfo.setUserId(principalUser.getUserId());
                    revInfo.setSuperUser(principalUser.isSuperUser());
                }
            }
        }
    }

}
