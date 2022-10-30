package com.nextlabs.destiny.console.services.policy.impl;

import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildFolderFilterQuery;
import static com.nextlabs.destiny.console.utils.SecurityContextUtil.getCurrentUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.config.root.PrincipalUser;
import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.policymgmt.FolderDTO;
import com.nextlabs.destiny.console.dto.policymgmt.FolderLite;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.enums.AuthorizableType;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.FolderType;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.ApplicationUser;
import com.nextlabs.destiny.console.model.delegadmin.AccessibleTags;
import com.nextlabs.destiny.console.model.delegadmin.ApplicableTag;
import com.nextlabs.destiny.console.model.policy.Folder;
import com.nextlabs.destiny.console.repositories.FolderRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.search.repositories.FolderSearchRepository;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;
import com.nextlabs.destiny.console.services.policy.FolderService;

/**
 * Service implementation for folder management.
 *
 * @author Sachindra Dasun
 */
@Service
public class FolderServiceImpl implements FolderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FolderServiceImpl.class);

    private static final FolderDTO POLICY_ROOT_FOLDER = new FolderDTO(-1L, "/", FolderType.POLICY);
    private static final FolderDTO COMPONENT_ROOT_FOLDER = new FolderDTO(-1L, "/", FolderType.COMPONENT);

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderSearchRepository folderSearchRepository;

    @Autowired
    private ApplicationUserSearchRepository applicationUserSearchRepository;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private EntityAuditLogDao entityAuditLogDao;

    /**
     * When the delegation rule has include sub-folders option, this method add all the sub folders to the rule.
     *
     * @param accessibleTags accessible tags
     */
    public void addIncludedSubFolderTags(AccessibleTags accessibleTags) {
        if (accessibleTags == null) {
            return;
        }
        accessibleTags.getTags().forEach(obligationTag -> {
            obligationTag.getViewTags().forEach(this::addIncludedSubFolderTagsToApplicableTag);
            obligationTag.getEditTags().forEach(this::addIncludedSubFolderTagsToApplicableTag);
            obligationTag.getDeleteTags().forEach(this::addIncludedSubFolderTagsToApplicableTag);
            obligationTag.getDeployTags().forEach(this::addIncludedSubFolderTagsToApplicableTag);
            obligationTag.getMoveTags().forEach(this::addIncludedSubFolderTagsToApplicableTag);
            obligationTag.getInsertTags().forEach(this::addIncludedSubFolderTagsToApplicableTag);
        });
    }

    private void addIncludedSubFolderTagsToApplicableTag(ApplicableTag applicableTag) {
        List<TagDTO> includedSubFolderTags = new ArrayList<>();
        applicableTag.getTags().forEach(tagDTO -> {
            if (TagType.FOLDER_TAG.name().equals(tagDTO.getType()) && tagDTO.getKey().endsWith("/**")) {
                tagDTO.setKey(tagDTO.getKey().replace("/**", ""));
                tagDTO.setLabel(tagDTO.getKey());
                findAllSubFolders(Long.parseLong(tagDTO.getKey()))
                        .forEach(folderDTO -> includedSubFolderTags
                                .add(new TagDTO(folderDTO.getId().toString(),
                                        folderDTO.getId().toString(), TagType.FOLDER_TAG.name())));
            }
        });
        applicableTag.getTags().addAll(includedSubFolderTags);
    }

    /**
     * Return the list of all folders found for the given type.
     *
     * @param folderType folder type can be policy/component
     * @return the list of folders found
     */
    @Override
    public List<FolderDTO> all(FolderType folderType) {
        return createFolderTree(findByType(folderType));
    }

    /**
     * Return the list of all folders found for the given type.
     *
     * @param folderType folder type can be policy/component
     * @return the list of folders found
     */
    @Override
    public List<FolderDTO> findByType(FolderType folderType) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("type", folderType.name()));
        NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder().withQuery(query);
        appendAccessControlTags(folderType, nativeQuery);
        List<FolderDTO> folderDTOS = new ArrayList<>();
        Page<FolderLite> folderLitePage = null;
        Pageable pageable = PageRequest.of(0, 10000);
        do {
            if (pageable != null) {
                folderLitePage = folderSearchRepository.search(nativeQuery.withPageable(pageable).build());
                folderDTOS.addAll(folderLitePage.get().map(FolderDTO::new).collect(Collectors.toList()));
                pageable = folderLitePage.hasNext() ? folderLitePage.nextPageable() : null;
            }
        } while (folderLitePage.hasNext());
        folderDTOS.add(0, new FolderDTO(-1L, "/", folderType));
        if (FolderType.POLICY.equals(folderType)) {
            accessControlService.enforceTBAConPolicyFolder(folderDTOS);
        } else {
            accessControlService.enforceTBAConComponentFolder(folderDTOS);
        }
        return folderDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFolderPath(FolderType folderType, String folderPath) throws ConsoleException {
        if (folderPath == null || folderPath.isEmpty()) {
            return null;
        }
        List<String> folderNamesInPath = Arrays.stream(folderPath.split("/"))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        List<FolderLite> allFolders = new ArrayList<>();
        folderSearchRepository.findByType(folderType.name()).forEach(allFolders::add);
        FolderLite folderFound = null;
        List<Long> parentIds = new ArrayList<>();

        accessControlService.checkAuthority(folderType == FolderType.COMPONENT ?
                        DelegationModelActions.CREATE_COMPONENT_FOLDER :
                        DelegationModelActions.CREATE_POLICY_FOLDER, ActionType.INSERT,
                        folderType == FolderType.COMPONENT ? AuthorizableType.COMPONENT_FOLDER :
                        AuthorizableType.POLICY_FOLDER);

        for (int i = 0; i < folderNamesInPath.size(); i++) {
            String currentFolderPath = String.join("/", folderNamesInPath.subList(0, i + 1));
            folderFound = findFolderByFolderPath(allFolders, currentFolderPath);
            if (folderFound == null) {
                FolderDTO folderDTO = new FolderDTO();
                folderDTO.setName(folderNamesInPath.get(i));
                folderDTO.setType(folderType);
                FolderLite parentFolder;
                if (i > 0) {
                    parentFolder = findFolderByFolderPath(allFolders,
                            String.join("/", folderNamesInPath.subList(0, i)));
                    folderDTO.setParentId(parentFolder == null ? null : parentFolder.getFolderId());
                }
                folderDTO = save(folderDTO);
                if(folderDTO != null) {
                    reIndexFolders(Collections.singleton(folderDTO.getId()));
                    folderFound = new FolderLite(folderDTO);
                    folderFound.setFolderPath(currentFolderPath);
                    folderFound.setParentIds(parentIds);
                    folderSearchRepository.save(folderFound);
                    allFolders.add(folderFound);
                }
            }
            if(folderFound != null) {
                parentIds.add(0, folderFound.getFolderId());
            }
        }
        return folderFound == null ? null : folderFound.getFolderId();
    }

    private FolderLite findFolderByFolderPath(List<FolderLite> folderLites, String folderPath) {
        FolderLite folderFound = null;
        for (FolderLite folderLite : folderLites) {
            if (folderPath.equals(folderLite.getFolderPath())) {
                folderFound = folderLite;
                break;
            }
        }
        return folderFound;
    }

    /**
     * Delete the given list of folders.
     *
     * @param ids folder id list to delete
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> delete(List<Long> ids) throws ConsoleException {
        List<Folder> folders = new ArrayList<>();
        for (Folder folder : folderRepository.findAllById(ids)) {
            accessControlService.authorizeByTags(ActionType.DELETE,
                    FolderType.POLICY.equals(folder.getType()) ? DelegationModelShortName.POLICY_FOLDER_ACCESS_TAGS :
                            DelegationModelShortName.COMPONENT_FOLDER_ACCESS_TAGS,
                    folder, false);
            folders.add(folder);
        }

        folders.forEach(folder -> {
            folderRepository.delete(folder);
            try {
                entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE,
                        FolderType.POLICY.equals(folder.getType()) ? AuditableEntity.POLICY_FOLDER.getCode() :
                                AuditableEntity.COMPONENT_FOLDER.getCode(), folder.getId(), folder.toAuditString(), "");
            } catch (ConsoleException e) {
                LOGGER.warn("Error in auditing folder action", e);
            }
        });
        return ids;
    }

    /**
     * Check if the folder with given name exists inside the  given folder.
     *
     * @param folderType     folder type can be policy/component
     * @param parentFolderId parent folder id to search
     * @param name           folder name
     * @return true if folder with same name exists
     */
    @Override
    public boolean folderExistsWithName(FolderType folderType, Long parentFolderId, String name) {
        return (parentFolderId == null ?
                folderRepository.findByTypeAndParentIdIsNull(folderType) :
                folderRepository.findById(parentFolderId).map(Folder::getChildren).orElse(new ArrayList<>()))
                .stream().anyMatch(childFolder -> name.equals(childFolder.getName()));
    }

    /**
     * Check if given folder exists.
     *
     * @param id folder id to search
     * @return true if folder exists
     */
    @Override
    public boolean exists(Long id) {
        return folderRepository.findById(id).isPresent();
    }

    /**
     * Find all the sub folders of the given folder.
     *
     * @param folderId given folder id
     * @return the list of sub folders
     */
    @Override
    public List<FolderDTO> findAllSubFolders(Long folderId) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .must(folderId == null ? QueryBuilders.matchAllQuery() :
                        QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("folderId", folderId))
                        .should(QueryBuilders.matchQuery("parentIds", folderId)));
        NativeSearchQueryBuilder nativeQuery = new NativeSearchQueryBuilder().withQuery(query);

        List<FolderDTO> folderDTOS = new ArrayList<>();
        Page<FolderLite> folderLitePage = null;
        Pageable pageable = PageRequest.of(0, 10000);
        do {
            if (pageable != null) {
                folderLitePage = folderSearchRepository.search(nativeQuery.withPageable(pageable).build());
                folderDTOS.addAll(folderLitePage.get().map(FolderDTO::new).collect(Collectors.toList()));
                pageable = folderLitePage.hasNext() ? folderLitePage.nextPageable() : null;
            }
        } while (folderLitePage.hasNext());
        return folderDTOS;
    }

    @Override
    public FolderDTO findById(Long id) {
        return folderRepository.findById(id).map(FolderDTO::new).orElse(null);
    }

    /**
     * Check if one folder is a sub folder of another.
     *
     * @param parentFolderId parent folder id
     * @param childFolderId  child folder id
     * @return true if the child folder is a sub folder of the parent folder
     */
    @Override
    public boolean isSubFolder(Long parentFolderId, Long childFolderId) {
        FolderDTO parentFolderDTO = folderRepository.findById(parentFolderId).map(FolderDTO::new).orElse(null);
        FolderDTO childFolderDTO = folderRepository.findById(childFolderId).map(FolderDTO::new).orElse(null);
        return parentFolderDTO != null
                && childFolderDTO != null
                && childFolderDTO.getFolderPath().startsWith(parentFolderDTO.getFolderPath());
    }

    /**
     * Check if sub-folders exists for the given list of folder ids
     *
     * @param ids folder ids to search
     * @return true if sub-folders found
     */
    @Override
    public boolean isSubFoldersFound(List<Long> ids) {
        return !folderRepository.findByParentIdIn(ids).isEmpty();
    }

    /**
     * Move a list of folders to another folder.
     *
     * @param destinationFolderId destination folder id
     * @param ids                 folder ids to move
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Set<Long> move(Long destinationFolderId, List<Long> ids) throws ConsoleException {
        List<Folder> folders = new ArrayList<>();
        Set<Long> affectedFolderIds = new HashSet<>();
        FolderType folderType = null;
        for (Folder folder : folderRepository.findAllById(ids)) {
            accessControlService.authorizeByTags(ActionType.MOVE,
                    FolderType.POLICY.equals(folder.getType()) ? DelegationModelShortName.POLICY_FOLDER_ACCESS_TAGS :
                            DelegationModelShortName.COMPONENT_FOLDER_ACCESS_TAGS,
                    folder, false);
            folderType = folder.getType();
            folders.add(folder);
        }
        FolderDTO destinationFolder;
        if (destinationFolderId == null) {
            destinationFolder = FolderType.POLICY.equals(folderType) ? POLICY_ROOT_FOLDER : COMPONENT_ROOT_FOLDER;
        } else {
            destinationFolder = folderRepository.findById(destinationFolderId).map(FolderDTO::new).orElse(null);
        }
        if (destinationFolder != null) {
            accessControlService.authorizeByTags(ActionType.INSERT,
                    FolderType.POLICY.equals(folderType) ? DelegationModelShortName.POLICY_FOLDER_ACCESS_TAGS :
                            DelegationModelShortName.COMPONENT_FOLDER_ACCESS_TAGS,
                    destinationFolder, false);
            folders.forEach(folder -> {
                String folderJsonBefore = folder.toAuditString();
                folder.setParentId(destinationFolderId);
                folderRepository.save(folder);
                try {
                    entityAuditLogDao.addEntityAuditLog(AuditAction.MOVE,
                            FolderType.POLICY.equals(folder.getType()) ? AuditableEntity.POLICY_FOLDER.getCode() :
                                    AuditableEntity.COMPONENT_FOLDER.getCode(), folder.getId(), folderJsonBefore,
                            folder.toAuditString(destinationFolder.getFolderPath()));
                } catch (ConsoleException e) {
                    LOGGER.warn("Error in auditing folder action", e);
                }
            });
            ids.forEach(id -> affectedFolderIds
                    .addAll(findAllSubFolders(id).stream()
                            .map(FolderDTO::getId)
                            .collect(Collectors.toList())
                    )
            );
            if (destinationFolderId != null) {
                affectedFolderIds.add(destinationFolderId);
            }
        }
        return affectedFolderIds;
    }

    /**
     * Rename folder.
     *
     * @param id      folder id to rename
     * @param newName new name of the folder
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rename(Long id, String newName) throws ConsoleException {
        Folder folder = folderRepository.findById(id).orElse(null);
        if (folder != null) {
            accessControlService.authorizeByTags(ActionType.RENAME,
                    FolderType.POLICY.equals(folder.getType()) ? DelegationModelShortName.POLICY_FOLDER_ACCESS_TAGS :
                            DelegationModelShortName.COMPONENT_FOLDER_ACCESS_TAGS,
                    folder, false);
            String folderJsonBefore = folder.toAuditString();
            folder.setName(newName);
            folderRepository.save(folder);
            try {
                entityAuditLogDao.addEntityAuditLog(AuditAction.RENAME,
                        FolderType.POLICY.equals(folder.getType()) ? AuditableEntity.POLICY_FOLDER.getCode() :
                                AuditableEntity.COMPONENT_FOLDER.getCode(), folder.getId(), folderJsonBefore,
                        folder.toAuditString());
            } catch (ConsoleException e) {
                LOGGER.warn("Error in adding audit record for rename folder action", e);
            }
            reIndexFolders(Collections.singleton(id));
        }
    }

    /**
     * Save folder.
     *
     * @param folderDTO folder to save
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FolderDTO save(FolderDTO folderDTO) throws ConsoleException {
        FolderDTO parentFolderDTO;
        if (folderDTO.getParentId() == null) {
            parentFolderDTO = FolderType.POLICY.equals(folderDTO.getType()) ? POLICY_ROOT_FOLDER : COMPONENT_ROOT_FOLDER;
        } else {
            parentFolderDTO = folderRepository.findById(folderDTO.getParentId()).map(FolderDTO::new).orElse(null);
        }
        if (parentFolderDTO != null) {
            accessControlService.authorizeByTags(ActionType.INSERT,
                    FolderType.POLICY.equals(parentFolderDTO.getType()) ? DelegationModelShortName.POLICY_FOLDER_ACCESS_TAGS :
                            DelegationModelShortName.COMPONENT_FOLDER_ACCESS_TAGS,
                    parentFolderDTO, false);
        }
        Folder folder = folderRepository.save(new Folder(folderDTO));
        try {
            entityAuditLogDao.addEntityAuditLog(AuditAction.CREATE,
                    FolderType.POLICY.equals(folder.getType()) ? AuditableEntity.POLICY_FOLDER.getCode() :
                            AuditableEntity.COMPONENT_FOLDER.getCode(), folder.getId(), null,
                    folder.toAuditString());
        } catch (ConsoleException e) {
            LOGGER.warn("Error in adding audit record for folder insert action", e);
        }
        folder = folderRepository.findById(folder.getId()).orElse(null);
        return folder != null ? new FolderDTO(folder) : null;
    }

    /**
     * Re-index all folders to Elasticsearch.
     */
    @Override
    public void reIndexAllFolders() throws ConsoleException {
        try {
            folderSearchRepository.deleteAll();
            List<FolderLite> folderLites = folderRepository.findAll().stream()
                    .map(FolderLite::new)
                    .collect(Collectors.toList());
            if (!folderLites.isEmpty()) {
                folderSearchRepository.saveAll(folderLites);
            }
        } catch (Exception e) {
            throw new ConsoleException("Error encountered in folder re-indexing", e);
        }
    }

    /**
     * Re-index the folders with given ids.
     *
     * @param folderIds folders id set to re-index
     */
    @Override
    public void reIndexFolders(Set<Long> folderIds) {
        folderIds.forEach(folderId -> {
            folderSearchRepository.deleteById(folderId);
        });
        folderRepository.findAllById(folderIds).stream()
                .map(FolderLite::new)
                .forEach(folderLite -> folderSearchRepository.save(folderLite));
    }

    private List<FolderDTO> createFolderTree(List<FolderDTO> folderDTOS) {
        for (FolderDTO currentFolderDTO : folderDTOS) {
            if (currentFolderDTO.getParentId() != null) {
                folderDTOS.stream().filter(folderDTO -> currentFolderDTO.getParentId().equals(folderDTO.getId()))
                        .findAny().ifPresent(folderDTO -> folderDTO.getChildren().add(currentFolderDTO));
            }
        }
        folderDTOS.removeIf(folderDTO -> folderDTO.getParentId() != null);
        return folderDTOS;
    }

    private void appendAccessControlTags(FolderType folderType, NativeSearchQueryBuilder nativeQuery) {
        PrincipalUser principal = getCurrentUser();
        if (principal.isSuperUser()) {
            LOGGER.debug("Access control filters do not applicable for super user");
            return;
        }

        ApplicationUser user = applicationUserSearchRepository.findById(principal.getUserId()).orElse(null);
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }

        AccessibleTags accessibleTags = FolderType.POLICY.equals(folderType) ? user.getPolicyFolderAccessibleTags() :
                user.getComponentFolderAccessibleTags();
        addIncludedSubFolderTags(accessibleTags);
        QueryBuilder foldersFilter = buildFolderFilterQuery(accessibleTags);
        if (foldersFilter == null) {
            LOGGER.info("Folder access control not found or not applicable for user, [ user : {}]", user.getUsername());
            return;
        }
        LOGGER.debug("Access control folder filter: {}", foldersFilter);
        nativeQuery.withFilter(QueryBuilders.boolQuery().must(foldersFilter));
    }

}
