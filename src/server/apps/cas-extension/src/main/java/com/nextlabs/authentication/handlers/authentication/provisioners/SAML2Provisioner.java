package com.nextlabs.authentication.handlers.authentication.provisioners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.authentication.enums.AuthType;
import com.nextlabs.authentication.enums.UserStatus;
import com.nextlabs.authentication.enums.UserType;
import com.nextlabs.authentication.models.AppUserProperties;
import com.nextlabs.authentication.models.ApplicationUser;
import com.nextlabs.authentication.models.AuthHandlerRegistry;
import com.nextlabs.authentication.repositories.AppUserPropertiesRepository;
import com.nextlabs.authentication.repositories.ApplicationUserDomainRepository;
import com.nextlabs.authentication.repositories.ApplicationUserRepository;
import com.nextlabs.authentication.repositories.AuthHandlerRegistryRepository;
import com.nextlabs.authentication.repositories.ProvisionedUserGroupRepository;
import org.apache.commons.lang.StringUtils;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.principal.ClientCredential;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.login.FailedLoginException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Component("SAML2Provisioner")
public class SAML2Provisioner
        implements Provisioner {

    private static final Logger logger = LoggerFactory.getLogger(SAML2Provisioner.class);

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private AppUserPropertiesRepository userPropertiesRepository;

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
        AuthHandlerRegistry authHandlerRegistry = handlerRepository.findByTypeIgnoreCase(AuthType.SAML2.name()).get(0);
        try {
            Map<String, String> userAttributes = getUserAttributes(clientCredential, authHandlerRegistry.getUserAttrsJson());

            if (users.isPresent()) {
                if(users.get().getAuthHandlerId() != authHandlerRegistry.getId()) {
                    throw new FailedLoginException("User has been authenticated externally but user source mismatch. Deny user from accessing the system.");
                }

                updateUser(users.get(), clientCredential.getUserProfile(), userAttributes);
                userPropertiesRepository.removeByUserId(users.get().getId());
                setUserAttributes(users.get(), userAttributes);
            } else {
                setUserAttributes(createUser(userAttributes, authHandlerRegistry), userAttributes);
            }
        } catch (JsonProcessingException err) {
            logger.error(err.getMessage(), err);
            throw new FailedLoginException("User has been authenticated externally but unable to be provisioned into the system. Deny user from accessing the system.");
        }
    }

    private Map<String, String> getUserAttributes(ClientCredential clientCredential, String userAttributesJson)
                    throws JsonProcessingException {
        Map<String, String> userAttributes = new HashMap<>();

        for(ComplexUserAttribute complexUserAttribute : new ObjectMapper().readValue(userAttributesJson,
                        ComplexUserAttribute[].class)) {
            Object attribute = clientCredential.getUserProfile().getAttribute(complexUserAttribute.getMappedAs());

            if(attribute != null) {
                String value = attribute.toString().trim();
                if(attribute instanceof Collection<?>
                        && ((Collection<?>) attribute).stream().collect(toList()).size() == 1
                        && value.startsWith("[")
                        && value.endsWith("]")) {
                    userAttributes.put(complexUserAttribute.getMappedAs(),
                                    value.substring(1, value.length() - 1).trim());
                } else {
                    userAttributes.put(complexUserAttribute.getMappedAs(), value);
                }
            }
        }

        return userAttributes;
    }

    private ApplicationUser createUser(Map<String, String> userAttributes, AuthHandlerRegistry authenticationHandler) {
        ApplicationUser applicationUser = new ApplicationUser();

        String firstName = StringUtils.isNotBlank(userAttributes.get("firstName")) ? userAttributes.get("firstName") : " ";
        String lastName = StringUtils.isNotBlank(userAttributes.get("lastName")) ? userAttributes.get("lastName") : " ";
        String displayName = StringUtils.isNotBlank(userAttributes.get("displayName")) ? userAttributes.get("displayName")
                : (String.format("%s %s", firstName, lastName)).trim();

        if(StringUtils.isBlank(displayName)) {
            displayName = userAttributes.get("username");
        }

        applicationUser.setUserType("imported");
        applicationUser.setUsername(userAttributes.get("username"));
        applicationUser.setFirstName(firstName);
        applicationUser.setLastName(lastName);
        applicationUser.setStatus(UserStatus.ACTIVE);
        applicationUser.setVersion(1);
        applicationUser.setDomainId(userDomainRepository.findByNameIgnoreCase("local").getId());
        applicationUser.setDisplayName(displayName);
        applicationUser.setEmail(userAttributes.get("email"));
        applicationUser.setFailedLoginAttempts(0);
        applicationUser.setLocked(false);
        applicationUser.setHideSplash(true);
        applicationUser.setCreatedBy(0L);
        applicationUser.setCreatedDate(new Date());
        applicationUser.setLastUpdatedBy(0L);
        applicationUser.setLastUpdated(new Date());
        applicationUser.setAuthHandlerId(authenticationHandler.getId());
        applicationUser.setManualProvision(false);

        return userRepository.save(applicationUser);
    }

    private void updateUser(ApplicationUser user, UserProfile userProfile, Map<String, String> userAttributes) {
        boolean modified = false;
        String firstName = StringUtils.isNotBlank(userAttributes.get("firstName")) ? userAttributes.get("firstName") : " ";
        if(!firstName.equals(user.getFirstName())) {
            user.setFirstName(firstName);
            modified = true;
        }
        String lastName = StringUtils.isNotBlank(userAttributes.get("lastName")) ? userAttributes.get("lastName") : " ";
        if(!lastName.equals(user.getLastName())) {
            user.setLastName(lastName);
            modified = true;
        }
        String displayName = StringUtils.isNotBlank(userAttributes.get("displayName")) ? userAttributes.get("displayName")
                : (String.format("%s %s", firstName, lastName)).trim();
        if(StringUtils.isBlank(displayName)) {
            displayName = userAttributes.get("username");
        }
        if(!displayName.equals(user.getDisplayName())) {
            user.setDisplayName(displayName);
            modified = true;
        }
        String email = userAttributes.get("email");
        if((email != null && !email.equals(user.getEmail()))
                || (user.getEmail() != null && !user.getEmail().equals(email))) {
            user.setEmail(email);
            modified = true;
        }

        if(modified) {
            user.setLastUpdatedBy(0L);
            user.setLastUpdated(new Date());
            userRepository.save(user);
        }
    }

    private void setUserAttributes(ApplicationUser applicationUser, Map<String, String> userProfile) {
        List<AppUserProperties> userProperties = new ArrayList<>();
        for(Map.Entry<String, String> userAttribute : userProfile.entrySet()) {
            if(userAttribute.getKey().equals("username")
                    || userAttribute.getKey().equals("firstName")
                    || userAttribute.getKey().equals("lastName")
                    || userAttribute.getKey().equals("email")) {
                continue;
            }

            AppUserProperties property = new AppUserProperties();
            property.setCreatedBy(0L);
            property.setCreatedDate(new Date());
            property.setLastUpdatedBy(0L);
            property.setLastUpdated(new Date());
            property.setVersion(0);
            property.setDataType("STRING");
            property.setPropKey(userAttribute.getKey());
            property.setPropValue(userAttribute.getValue());
            property.setUserId(applicationUser.getId());

            userProperties.add(property);
        }

        userPropertiesRepository.saveAll(userProperties);
    }
}

class ComplexUserAttribute {

    private String name;

    private String friendlyName;

    private String nameFormat;

    private boolean required;

    private String mappedAs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getNameFormat() {
        return nameFormat;
    }

    public void setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getMappedAs() {
        return mappedAs;
    }

    public void setMappedAs(String mappedAs) {
        this.mappedAs = mappedAs;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(new String[] { "aaa"}));
    }
}
