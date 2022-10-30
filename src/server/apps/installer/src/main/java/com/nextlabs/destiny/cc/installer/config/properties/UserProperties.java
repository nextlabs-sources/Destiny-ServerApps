package com.nextlabs.destiny.cc.installer.config.properties;

import javax.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;

/**
 * Properties available to configure Control Center user in Linux.
 *
 * @author Sachindra Dasun
 */
@Validated
public class UserProperties {

    @NotEmpty(message = "{user.group.notEmpty}")
    private String group;
    @NotEmpty(message = "{user.home.notEmpty}")
    private String home;
    @NotEmpty(message = "{user.name.notEmpty}")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

}
