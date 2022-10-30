/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 15, 2015
 *
 */
package com.nextlabs.destiny.console.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elastic search client connection configurations
 *
 * @author Amila Silva
 * @since 8.0
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = {
        "com.nextlabs.destiny.console.search.repositories"})
public class TestElasticSearchConfig {

}
