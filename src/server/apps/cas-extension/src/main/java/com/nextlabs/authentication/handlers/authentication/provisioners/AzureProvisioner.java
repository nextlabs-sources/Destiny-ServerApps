package com.nextlabs.authentication.handlers.authentication.provisioners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.authentication.enums.AuthType;
import com.nextlabs.authentication.enums.UserStatus;
import com.nextlabs.authentication.enums.UserType;
import com.nextlabs.authentication.models.ApplicationUser;
import com.nextlabs.authentication.models.AuthHandlerRegistry;
import com.nextlabs.authentication.models.ProvisionedUserGroup;
import com.nextlabs.authentication.repositories.ApplicationUserDomainRepository;
import com.nextlabs.authentication.repositories.ApplicationUserRepository;
import com.nextlabs.authentication.repositories.AuthHandlerRegistryRepository;
import com.nextlabs.authentication.repositories.ProvisionedUserGroupRepository;
import net.minidev.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.principal.ClientCredential;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.login.FailedLoginException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component("AzureProvisioner")
public class AzureProvisioner
        implements Provisioner {

    private static final Logger logger = LoggerFactory.getLogger(AzureProvisioner.class);

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private AuthHandlerRegistryRepository handlerRepository;

    @Autowired
    private ProvisionedUserGroupRepository userGroupRepository;

    @Autowired
    private ApplicationUserDomainRepository userDomainRepository;

    @Override
    public void provisionUser(final ClientCredential clientCredential, final AuthenticationHandlerExecutionResult result)
            throws FailedLoginException {
        Optional<ApplicationUser> users = userRepository.findByUsernameIgnoreCaseAndUserTypeAndStatus(
                        result.getPrincipal().getId(), UserType.IMPORTED.getType(), UserStatus.ACTIVE);
        AuthHandlerRegistry authHandlerRegistry = handlerRepository.findByTypeIgnoreCase(AuthType.OIDC.name()).get(0);
        try {
            Map<String, String> userAttributeMap = new ObjectMapper().readValue(authHandlerRegistry.getUserAttrsJson(), new TypeReference<>() {});
            if (users.isPresent()) {
                if (users.get().getAuthHandlerId() != authHandlerRegistry.getId()) {
                    throw new FailedLoginException("User has been authenticated externally but user source mismatch. Deny user from accessing the system.");
                }

                if (!users.get().getManualProvision()) {
                    provisionByUserGroup(clientCredential, userAttributeMap, false);
                }
            } else {
                provisionByUserGroup(clientCredential, userAttributeMap, true);
            }
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            throw new FailedLoginException("Unable to recover user attributes mapping.");
        }
    }

    private void provisionByUserGroup(ClientCredential clientCredential, Map<String, String> userAttributeMap, boolean provisionUser)
            throws FailedLoginException {
        try {
            if (clientCredential.getUserProfile().containsAttribute("groups")) {
                JSONArray groups = (JSONArray)clientCredential.getUserProfile().getAttribute("groups");
                String groupJsonString = (String)groups.get(0);

                if(!groupJsonString.isEmpty()) {
                    org.json.JSONArray groupJson = new org.json.JSONArray(groupJsonString);
                    for (int i = 0; i < groupJson.length(); i++) {
                        Optional<ProvisionedUserGroup> provisionedUserGroup = userGroupRepository.findByGroupIdIgnoreCase(groupJson.getString(i));

                        if(provisionedUserGroup.isPresent()) {
                            if(provisionUser) {
                                createUser(clientCredential.getUserProfile(), userAttributeMap, provisionedUserGroup
                                                .get().getAuthHandlerId());
                            }

                            return;
                        }
                    }
                }
            }
        } catch(Exception err) {
            logger.error(err.getMessage(), err);
        }

        throw new FailedLoginException("User has been authenticated externally but not provisioned in the system. Deny user from accessing the system.");
    }

    private void createUser(UserProfile userProfile, Map<String, String> userAttributeMap, Long authenticationHandlerId) {
        ApplicationUser applicationUser = new ApplicationUser();
        String firstName = userProfile.getAttribute("given_name") != null
                ? (String) userProfile.getAttribute("given_name") : " ";
        String lastName = userProfile.getAttribute("family_name") != null
                ? (String) userProfile.getAttribute("family_name") : " ";
        String displayName = userProfile.getAttribute(userAttributeMap.get("displayName")) != null
                ? (String) userProfile.getAttribute(userAttributeMap.get("displayName")) : String.format("%s %s", firstName, lastName);
        if(StringUtils.isBlank(displayName)) {
            displayName = (String) userProfile.getAttribute("upn");
        }
        String email = userProfile.getAttribute(userAttributeMap.get("email")) != null
                ? (String) userProfile.getAttribute(userAttributeMap.get("email")) : null;


        applicationUser.setUserType("imported");
        applicationUser.setUsername((String) userProfile.getAttribute("upn"));
        applicationUser.setFirstName(firstName);
        applicationUser.setLastName(lastName);
        applicationUser.setStatus(UserStatus.ACTIVE);
        applicationUser.setVersion(1);
        applicationUser.setDomainId(userDomainRepository.findByNameIgnoreCase("local").getId());
        applicationUser.setDisplayName(displayName);
        applicationUser.setEmail(email);
        applicationUser.setFailedLoginAttempts(0);
        applicationUser.setLocked(false);
        applicationUser.setHideSplash(true);
        applicationUser.setCreatedBy(-1L);
        applicationUser.setCreatedDate(new Date());
        applicationUser.setLastUpdatedBy(-1L);
        applicationUser.setLastUpdated(new Date());
        applicationUser.setAuthHandlerId(authenticationHandlerId);
        applicationUser.setManualProvision(false);

        userRepository.save(applicationUser);
    }
}
