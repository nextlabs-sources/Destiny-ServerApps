/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Dec 15, 2015
 *
 */
package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.model.TagLabel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * Tag Label Search repository
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public interface TagLabelSearchRepository
        extends ElasticsearchRepository<TagLabel, Long> {

    Page<TagLabel> findByKeyAndTypeAndStatusAndHiddenOrderByLabelAsc(String key,
            TagType tagType, Status status, boolean hidden, Pageable pageable);

    Page<TagLabel> findByTypeAndStatusAndHiddenOrderByLabelAsc(TagType tagType,
            Status status, boolean hidden, Pageable pageable);

    Page<TagLabel> findByTypeAndStatusOrderByLabelAsc(TagType tagType,
            Status status, Pageable pageable);

}
