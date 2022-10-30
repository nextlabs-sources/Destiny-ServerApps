package com.nextlabs.destiny.console.exceptions;

import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import com.nextlabs.destiny.console.dto.delegadmin.ForbiddenResponse;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuthorizableType;

/**
 * Thrown by access control service if the user does not posses necessary permissions to perform a particular action.
 *
 * @author Sachindra Dasun
 */
public class ForbiddenException extends SecurityException {

    public static final String UNAUTHORIZED_REQUEST = "server.request.not.authorized.code";
    public static final String MISSING_POLICY_TAGS_IN_REQUEST = "server.request.not.authorized.missing.policy.tags" +
            ".code";
    public static final String MISSING_COMPONENT_TAGS_IN_REQUEST = "server.request.not.authorized.missing.component" +
            ".tags.code";
    public static final String MISSING_POLICY_MODEL_TAGS_IN_REQUEST = "server.request.not.authorized.missing.policy" +
            ".model.tags.code";
    private static final long serialVersionUID = -430133875102984598L;
    private final String errorCode;
    private final ActionType actionType;
    private final AuthorizableType authorizableType;
    private final String componentType;
    private final String componentName;

    public ForbiddenException(String errorCode, String message, ActionType actionType, AuthorizableType authorizableType) {
        super(message);
        this.errorCode = errorCode;
        this.actionType = actionType;
        this.authorizableType = authorizableType;
        this.componentType = null;
        this.componentName = null;
    }

    public ForbiddenException(String errorCode, String message, ActionType actionType,
                              AuthorizableType authorizableType,
                              String componentType, String componentName) {
        super(message);
        this.errorCode = errorCode;
        this.actionType = actionType;
        this.authorizableType = authorizableType;
        this.componentType = componentType;
        this.componentName = componentName;
    }

    public ForbiddenResponse getForbiddenResponse() {
        return new ForbiddenResponse(getErrorCode(), getActionType(), getAuthorizableType(), getComponentType(),
                getComponentName(), getMessage());
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

    @Override
    public String toString() {
        StringJoiner text = new StringJoiner(", ", "(", ")");
        if (actionType != null) {
            text.add(String.format("Action Type: %s", actionType));
        }
        if (StringUtils.isNotEmpty(componentType)) {
            text.add(String.format("Component Type: %s", componentType));
        }
        if (StringUtils.isNotEmpty(componentName)) {
            text.add(String.format("Component Name: %s", componentName));
        }
        return text.toString();
    }

}
