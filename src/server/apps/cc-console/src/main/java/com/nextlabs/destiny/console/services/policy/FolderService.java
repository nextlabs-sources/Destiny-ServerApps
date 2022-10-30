package com.nextlabs.destiny.console.services.policy;

import java.util.List;
import java.util.Set;

import com.nextlabs.destiny.console.dto.policymgmt.FolderDTO;
import com.nextlabs.destiny.console.enums.FolderType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.delegadmin.AccessibleTags;

/**
 * Service interface for folder management.
 *
 * @author Sachindra Dasun
 */
public interface FolderService {

    void addIncludedSubFolderTags(AccessibleTags accessibleTags);

    List<FolderDTO> all(FolderType folderType);

    Long createFolderPath(FolderType folderType, String folderPath) throws ConsoleException;

    List<Long> delete(List<Long> ids) throws ConsoleException;

    boolean exists(Long id);

    List<FolderDTO> findAllSubFolders(Long folderId);

    FolderDTO findById(Long id);

    List<FolderDTO> findByType(FolderType folderType);

    boolean folderExistsWithName(FolderType folderType, Long parentFolderId, String name);

    boolean isSubFolder(Long parentFolderId, Long childFolderId);

    boolean isSubFoldersFound(List<Long> id);

    Set<Long> move(Long destinationFolderId, List<Long> ids) throws ConsoleException;

    void rename(Long id, String newName) throws ConsoleException;

    FolderDTO save(FolderDTO folderDTO) throws ConsoleException;

    void reIndexAllFolders() throws ConsoleException;

    void reIndexFolders(Set<Long> folderIds);

}

