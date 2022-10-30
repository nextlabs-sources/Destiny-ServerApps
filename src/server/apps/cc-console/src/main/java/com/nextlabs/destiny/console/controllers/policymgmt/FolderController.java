package com.nextlabs.destiny.console.controllers.policymgmt;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.controllers.AbstractRestController;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.FolderDTO;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.FolderType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;
import com.nextlabs.destiny.console.services.policy.ComponentSearchService;
import com.nextlabs.destiny.console.services.policy.FolderService;
import com.nextlabs.destiny.console.services.policy.PolicyMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicySearchService;

/**
 * REST controller to manage folders.
 *
 * @author Sachindra Dasun
 */
@RestController
@ApiVersion(1)
@RequestMapping("folders")
public class FolderController extends AbstractRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FolderController.class);

    @Autowired
    private FolderService folderService;

    @Autowired
    private ComponentMgmtService componentMgmtService;

    @Autowired
    private PolicyMgmtService policyMgmtService;

    @Autowired
    private PolicySearchService policySearchService;

    @Autowired
    private ComponentSearchService componentSearchService;

    /**
     * Fetch all folders found for the type (component or policy).
     *
     * @param type folder type
     * @return the list of folders found
     */
    @GetMapping("all/{type}")
    public ConsoleResponseEntity<List<FolderDTO>> all(@PathVariable String type) {
        return ConsoleResponseEntity.get(folderService.all(FolderType.valueOf(type.toUpperCase())), HttpStatus.OK);
    }

    /**
     * Save the folder.
     *
     * @param folderDTO folder to save.
     * @return success if folder saved
     */
    @PostMapping("save/{type}")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> save(@RequestBody FolderDTO folderDTO) throws ConsoleException {
        String responseMessage = "success.data.saved";
        if (folderService.folderExistsWithName(folderDTO.getType(), folderDTO.getParentId(), folderDTO.getName())) {
            responseMessage = "folder.exists.with.the.name";
        } else {
            FolderDTO savedFolderDTO = folderService.save(folderDTO);
            if(savedFolderDTO != null) {
                folderService.reIndexFolders(Collections.singleton(savedFolderDTO.getId()));
            }
        }
        return ConsoleResponseEntity.get(SimpleResponseDTO.createWithType(
                msgBundle.getCode(responseMessage),
                msgBundle.getText(responseMessage),
                msgBundle.getText(responseMessage)), HttpStatus.OK);
    }

    /**
     * Rename a folder.
     *
     * @param type     folder type
     * @param folderId folder id
     * @param name     new name of the folder
     * @return success response if folder is renamed successfully
     */
    @PutMapping("rename/{type}/{folderId}")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> rename(@PathVariable String type,
                                                                   @PathVariable Long folderId,
                                                                   @RequestBody String name) throws ConsoleException {
        String responseMessage = "success.data.saved";
        FolderDTO folderDTO = folderService.findById(folderId);
        if (folderDTO != null) {
            if (folderService.folderExistsWithName(FolderType.valueOf(type.toUpperCase()), folderDTO.getParentId(), name)) {
                responseMessage = "folder.exists.with.the.name";
            } else {
                folderService.rename(folderId, name);
            }
        } else {
            responseMessage = "no.data.found";
        }
        return ConsoleResponseEntity.get(SimpleResponseDTO.createWithType(
                msgBundle.getCode(responseMessage),
                msgBundle.getText(responseMessage),
                msgBundle.getText(responseMessage)), HttpStatus.OK);
    }

    /**
     * Move folder/policy/component from one folder to another. The destination folder cannot be a sub-folder.
     *
     * @param entityType          policy/component
     * @param destinationFolderId destination folder id
     * @param ids                 folder/policy/component ids to move
     * @return success response if folders/policies/components are moved successfully
     * @throws ConsoleException if an error occurred
     */
    @PutMapping("move/{type}/{entityType}/{destinationFolderId}")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> move(@PathVariable FolderType type,
                                                                 @PathVariable String entityType,
                                                                 @PathVariable Long destinationFolderId,
                                                                 @RequestBody List<Long> ids) throws ConsoleException {
        final Long folderId = destinationFolderId < 0 ? null : destinationFolderId;
        String responseMessage = "success.data.saved";
        if (folderId == null || folderService.exists(folderId)) {
            if (entityType.equalsIgnoreCase(DevEntityType.FOLDER.name())) {
                if (folderId == null || ids.stream().noneMatch(id -> folderService.isSubFolder(id, folderId))) {
                    if (ids.stream().noneMatch(id ->
                            folderService.folderExistsWithName(type, folderId, folderService.findById(id).getName()))) {
                        Set<Long> affectedFolderIds = folderService.move(folderId, ids);
                        folderService.reIndexFolders(affectedFolderIds);
                        for (Long id : affectedFolderIds) {
                            if (FolderType.POLICY.equals(type)) {
                                policySearchService.reIndexPoliciesByFolder(id);
                            } else {
                                componentSearchService.reIndexComponentsByFolder(id);
                            }
                        }
                    } else {
                        responseMessage = "folder.exists.with.the.name";
                    }
                } else {
                    responseMessage = "folder.cannot.move.to.subfolder";
                }
            } else if (entityType.equalsIgnoreCase(DevEntityType.COMPONENT.name())) {
                componentMgmtService.move(folderId, ids);
            } else {
                policyMgmtService.move(folderId, ids);
            }
        } else {
            responseMessage = "no.data.found";
        }
        return ConsoleResponseEntity.get(SimpleResponseDTO.createWithType(
                msgBundle.getCode(responseMessage),
                msgBundle.getText(responseMessage),
                msgBundle.getText(responseMessage)), HttpStatus.OK);
    }

    /**
     * Delete a list of folders.
     *
     * @param folderIds folder id list to delete
     * @return success response if folder deleted successfully
     */
    @PutMapping("delete/{type}")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> delete(@RequestBody List<Long> folderIds) throws ConsoleException {
        String responseMessage = "success.data.deleted";
        try {
            if (folderService.isSubFoldersFound(folderIds)) {
                responseMessage = "folder.cannot.delete.non.empty.folder";
            } else {
                List<Long> affectedFolderIds = folderService.delete(folderIds);
                folderService.reIndexFolders(new HashSet<>(affectedFolderIds));
            }
        } catch (DataIntegrityViolationException e) {
            responseMessage = "folder.cannot.delete.non.empty.folder";
        }
        return ConsoleResponseEntity.get(SimpleResponseDTO.createWithType(
                msgBundle.getCode(responseMessage),
                msgBundle.getText(responseMessage),
                msgBundle.getText(responseMessage)), HttpStatus.OK);
    }

    @Override
    protected Logger getLog() {
        return LOGGER;
    }

}
