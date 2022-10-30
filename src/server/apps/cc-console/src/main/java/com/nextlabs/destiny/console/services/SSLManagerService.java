package com.nextlabs.destiny.console.services;


import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 *
 * Service to manage SSL factory
 *
 * @author Mohammed Sainal Shah
 * @since 2020.08
 *
 */
public interface SSLManagerService {

    HttpComponentsClientHttpRequestFactory getRequestFactory();

}
