package com.nextlabs.destiny.console.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.enums.AuthorizableType;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.model.Tag;
import com.nextlabs.destiny.console.model.delegadmin.DelegateModel;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyModel;

public interface Authorizable {

    Set<? extends Tag> getTags();

    Long getFolderId();

    default Object getType() {
        return null;
    }

    default String getName() {
        return null;
    }

    @JsonIgnore
    default AuthorizableType getAuthorizableType() {
        if (this instanceof ComponentDTO) {
            return AuthorizableType.COMPONENT;
        } else if (this instanceof PolicyDTO) {
            return AuthorizableType.POLICY;
        } else if (this instanceof PolicyDevelopmentEntity) {
            return AuthorizableType.valueOf(DevEntityType.getByKey(getType().toString()).name());
        } else if (this instanceof PolicyModel) {
            return AuthorizableType.POLICY_MODEL;
        } else if (this instanceof DelegateModel) {
            return AuthorizableType.DELEGATE_MODEL;
        }
        return null;
    }
}
