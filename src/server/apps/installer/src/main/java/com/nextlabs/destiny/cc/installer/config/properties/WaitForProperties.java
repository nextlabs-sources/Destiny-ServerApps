package com.nextlabs.destiny.cc.installer.config.properties;

import com.nextlabs.destiny.cc.installer.annotations.ValidWaitFor;

/**
 * Properties available to configure wait for url.
 *
 * @author Sachindra Dasun
 */
@ValidWaitFor(message = "{waitFor.urlAccessible}")
public class WaitForProperties {

    private int retryAttempts;
    private int retryBackOffPeriod;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public int getRetryBackOffPeriod() {
        return retryBackOffPeriod;
    }

    public void setRetryBackOffPeriod(int retryBackOffPeriod) {
        this.retryBackOffPeriod = retryBackOffPeriod;
    }

}
