package com.nextlabs.authentication.repositories;

import com.nextlabs.authentication.models.FileResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for file resource.
 *
 * @author Chok Shah Neng
 * @since 2020.09
 */
@Repository
public interface FileResourceRepository extends CrudRepository<FileResource, Long> {
    List<FileResource> findByApplication(String application);
}
