package com.nextlabs.destiny.console.search.repositories;

import com.nextlabs.destiny.console.dto.policymgmt.FolderLite;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Folder search repository.
 *
 * @author Sachindra Dasun
 */
public interface FolderSearchRepository extends ElasticsearchRepository<FolderLite, Long> {

    Iterable<FolderLite> findByType(String type);
}
