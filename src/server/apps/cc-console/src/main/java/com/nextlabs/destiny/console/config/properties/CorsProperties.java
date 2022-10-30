package com.nextlabs.destiny.console.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Loading Cross Origin Resource Sharing (CORS) related configurations.
 * 
 * @author Moushumi Seal
 *
 */
@Component
@ConfigurationProperties(prefix = "security.cors")
public class CorsProperties {
	
	private String allowedOrigins;

	public String getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(String allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}
}
