package com.nextlabs.destiny.console.dto.delegadmin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuthorizableType;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
public class ForbiddenResponse implements Serializable {

    private static final long serialVersionUID = -6275420121759251544L;
    private String errorCode;
    private ActionType actionType;
    private AuthorizableType authorizableType;
    private String componentType;
    private String componentName;
    private String message;

    public ForbiddenResponse(String errorCode, ActionType actionType, AuthorizableType authorizableType,
                             String componentType, String componentName, String message) {
        this.errorCode = errorCode;
        this.actionType = actionType;
        this.authorizableType = authorizableType;
        this.componentType = componentType;
        this.componentName = componentName;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public AuthorizableType getAuthorizableType() {
        return authorizableType;
    }

    public String getComponentType() {
        return componentType;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getMessage() {
        return message;
    }

}
