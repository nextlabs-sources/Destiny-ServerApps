package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.nextlabs.destiny.cc.installer.annotations.ValidWaitFor;
import com.nextlabs.destiny.cc.installer.config.properties.WaitForProperties;
import com.nextlabs.destiny.cc.installer.exceptions.InstallerException;

/**
 * Validator to wait until URL is reachable.
 *
 * @author Sachindra Dasun
 */
public class WaitForValidator implements ConstraintValidator<ValidWaitFor, WaitForProperties> {

    private static final Marker INSTALLER_CONSOLE_MARKER = MarkerFactory.getMarker("INSTALLER_CONSOLE");
    private static final Logger logger = LoggerFactory.getLogger(WaitForValidator.class);

    @Override
    public boolean isValid(WaitForProperties waitForProperties, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(waitForProperties.getUrl())) {
            return true;
        }
        try {
            FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
            fixedBackOffPolicy.setBackOffPeriod(waitForProperties.getRetryBackOffPeriod());
            RetryTemplate retryTemplate = new RetryTemplate();
            retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
            retryTemplate.setRetryPolicy(new SimpleRetryPolicy(waitForProperties.getRetryAttempts()));
            logger.info(INSTALLER_CONSOLE_MARKER, "Waiting for URL: {}", waitForProperties.getUrl());
            return retryTemplate.execute(retryContext -> {
                try (CloseableHttpClient httpClient = HttpClients.custom()
                        .setSSLSocketFactory(new SSLConnectionSocketFactory(
                                SSLContextBuilder.create()
                                        .loadTrustMaterial(new TrustAllStrategy()).build(),
                                new NoopHostnameVerifier())).build()) {
                    try (CloseableHttpResponse httpResponse = httpClient.execute(new HttpGet(waitForProperties.getUrl()))) {
                        HttpStatus httpStatus = HttpStatus.valueOf(httpResponse.getStatusLine().getStatusCode());
                        if (httpStatus.is1xxInformational() || httpStatus.is2xxSuccessful() || httpStatus.is3xxRedirection()
                                || httpStatus.is4xxClientError()) {
                            return true;
                        }
                    }
                }
                throw new InstallerException("Unable to connect to: " + waitForProperties.getUrl());
            });
        } catch (Exception e) {
            logger.error("Error in wait for URL test", e);
        }
        return false;
    }

}
