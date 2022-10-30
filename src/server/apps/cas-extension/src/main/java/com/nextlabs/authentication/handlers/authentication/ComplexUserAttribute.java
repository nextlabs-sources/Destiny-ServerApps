package com.nextlabs.authentication.handlers.authentication;

public class ComplexUserAttribute {

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
}
