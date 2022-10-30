package com.nextlabs.destiny.console.config.root;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

/**
 * WebSoket configuration used to notify the CC console.
 *
 * @author Mohammed Sainal Shah
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void configureInbound(
            MessageSecurityMetadataSourceRegistry messages) {
        messages.simpDestMatchers("/secured/**").authenticated()
                .anyMessage().authenticated();
    }
}
