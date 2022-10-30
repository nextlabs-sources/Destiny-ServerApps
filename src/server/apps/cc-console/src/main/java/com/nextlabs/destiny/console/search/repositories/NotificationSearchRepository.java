/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 18, 2015
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.dto.notification.NotificationLite;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Notification Search criteria repository
 *
 * @author Mohammed Sainal Shah
 * @since 2020.10
 *
 */
public interface NotificationSearchRepository
        extends ElasticsearchRepository<NotificationLite, Long> {

}
