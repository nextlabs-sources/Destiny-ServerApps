/**
 * 
 */
package com.nextlabs.destiny.console.services.policy.impl;

import java.io.IOException;
import java.util.Properties;

import org.apache.openaz.pepapi.PepAgent;
import org.apache.openaz.pepapi.PepAgentFactory;
import org.apache.openaz.pepapi.std.StdPepAgentFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kyu
 * @since 8.0.8
 *
 */
@Configuration
public class PepAgentBuilder {
    
    @Bean
    public PepAgent getPepAgent() throws IOException {
        Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("openaz-pep.properties"));
        PepAgentFactory pepAgentFactory = new StdPepAgentFactory(properties);
        return pepAgentFactory.getPepAgent();
    }
}
