package com.nextlabs.authentication.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CORS configuration properties.
 *
 * @author Sachindra Dasun
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
