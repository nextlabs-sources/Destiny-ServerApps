package com.nextlabs.destiny.console.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Loading X-Frame-Options Header related configurations.
 * 
 * @author Moushumi Seal
 *
 */

@Component
@ConfigurationProperties(prefix = "security.xframe")
public class XFrameProperties {

	private String options;
	private String allowedOrigins;

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(String allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

}
