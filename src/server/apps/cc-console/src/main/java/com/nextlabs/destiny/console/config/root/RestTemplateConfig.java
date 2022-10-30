package com.nextlabs.destiny.console.config.root;

import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.services.SSLManagerService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 *
 * Rest template configuration
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
@Configuration
public class RestTemplateConfig {

    @RefreshScope
    @Bean
    public RestTemplate sslRestTemplate(SSLManagerService sslManagerService) throws ServerException {
        HttpComponentsClientHttpRequestFactory requestFactory = sslManagerService.getRequestFactory();
        if (requestFactory != null) {
            return new RestTemplate(requestFactory);
        }
        throw new ServerException("Error while setting up SSL for rest template");
    }
}
