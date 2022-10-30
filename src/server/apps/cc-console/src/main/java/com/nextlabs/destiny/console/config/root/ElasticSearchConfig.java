/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 15, 2015
 *
 */
package com.nextlabs.destiny.console.config.root;

import java.net.InetSocketAddress;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.nextlabs.destiny.console.config.properties.ElasticsearchProperties;

/**
 * Elastic search client connection configurations
 *
 * @author Amila Silva
 * @since 8.0
 */
@Configuration
@EnableElasticsearchRepositories("com.nextlabs.destiny.console.search.repositories")
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    private ElasticsearchProperties elasticsearchProperties;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(new InetSocketAddress(elasticsearchProperties.getHost(),
                        elasticsearchProperties.getPort()))
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Autowired
    public void setElasticsearchProperties(ElasticsearchProperties elasticsearchProperties) {
        this.elasticsearchProperties = elasticsearchProperties;
    }

}
