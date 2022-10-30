package com.nextlabs.destiny.console.config.listeners;

import com.nextlabs.destiny.console.dto.authentication.ActiveUserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    private final ActiveUserStore activeUserStore;

    private HttpSession httpSession;

    @Autowired
    public LoginListener(ActiveUserStore activeUserStore) {
        this.activeUserStore = activeUserStore;
    }

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        UserDetails user = (UserDetails) event.getAuthentication().getPrincipal();
        LoggedUser loggedUser = new LoggedUser(user.getUsername(), activeUserStore);
        httpSession.setAttribute("user", loggedUser);
    }

    @Autowired
    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }
}