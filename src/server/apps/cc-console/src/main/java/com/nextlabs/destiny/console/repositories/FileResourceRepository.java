package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.model.FileResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileResourceRepository
                extends JpaRepository<FileResource, Long> {

    Optional<FileResource> findByApplicationAndModuleAndKey(String application, String module, String key);

    List<FileResource> removeByApplicationAndModule(String application, String module);

}
