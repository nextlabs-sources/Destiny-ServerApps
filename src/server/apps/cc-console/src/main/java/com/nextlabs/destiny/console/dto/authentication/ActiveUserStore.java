package com.nextlabs.destiny.console.dto.authentication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ActiveUserStore implements Serializable {

    private static final long serialVersionUID = -478665822226705335L;

    private List<String> users;

    public ActiveUserStore() {
        users = new ArrayList<>();
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
