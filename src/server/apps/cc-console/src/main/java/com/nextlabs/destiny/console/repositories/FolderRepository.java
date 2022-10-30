package com.nextlabs.destiny.console.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextlabs.destiny.console.enums.FolderType;
import com.nextlabs.destiny.console.model.policy.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findByParentIdIn(List<Long> parentId);

    List<Folder> findByTypeAndParentIdIsNull(FolderType type);

}
