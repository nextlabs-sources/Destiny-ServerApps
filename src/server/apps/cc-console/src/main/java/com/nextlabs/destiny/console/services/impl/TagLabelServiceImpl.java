/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.services.impl;

import static com.nextlabs.destiny.console.enums.Status.ACTIVE;

import javax.annotation.Resource;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nextlabs.destiny.console.dao.TagLabelDao;
import com.nextlabs.destiny.console.delegadmin.actions.DelegationModelActions;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuthorizableType;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.search.repositories.TagLabelSearchRepository;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.TagLabelService;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;

/**
 *
 * Tag Label service implementation
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class TagLabelServiceImpl implements TagLabelService {

    private static final Logger log = LoggerFactory
            .getLogger(TagLabelServiceImpl.class);

    @Autowired
    private TagLabelDao tagLabelDao;

    @Resource
    private TagLabelSearchRepository tagLabelSearchRepository;

    @Autowired
    protected MessageBundleService msgBundle;

    @Autowired
    private AccessControlService accessControlService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public TagLabel saveTag(TagLabel tagLabel) throws ConsoleException {
        switch (tagLabel.getType()) {
            case POLICY_TAG:
                accessControlService.checkAuthority(DelegationModelActions.CREATE_POLICY_TAG, ActionType.INSERT,
                        AuthorizableType.POLICY_TAG);
                break;
            case COMPONENT_TAG:
                accessControlService.checkAuthority(DelegationModelActions.CREATE_COMPONENT_TAG, ActionType.INSERT,
                        AuthorizableType.COMPONENT_TAG);
                break;
            case POLICY_MODEL_TAG:
                accessControlService.checkAuthority(DelegationModelActions.CREATE_POLICY_MODEL_TAG, ActionType.INSERT,
                        AuthorizableType.POLICY_MODEL_TAG);
                break;
            default:
        }
        try {
            if (tagLabel.getId() == null) {
                tagLabelDao.create(tagLabel);
            } else {
                tagLabelDao.update(tagLabel);
            }
            tagLabelSearchRepository.save(tagLabel);

            log.debug("Tag Label saved successfully, [ Id: {}]",
                    tagLabel.getId());
        } catch (Exception e) {
            throw new ConsoleException("Error encountered while saving a Tag", e);
        }
        return tagLabel;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public TagLabel findById(Long id) throws ConsoleException {
        try {
            TagLabel tagLabel = tagLabelDao.findById(id);

            if (tagLabel == null) {
                log.info("Tag Label not found for id :{}", id);
            }

            return tagLabel;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find a Tag by id", e);
        }
    }

    @Override
    public Page<TagLabel> findByType(String tagType, boolean showHidden,
            Pageable pageable) throws ConsoleException {
        try {
            TagType type = TagType.getType(tagType);

            Page<TagLabel> tagLabelPages = null;
            if (showHidden) {
                tagLabelPages = tagLabelSearchRepository
                        .findByTypeAndStatusOrderByLabelAsc(type, ACTIVE,
                                pageable);
            } else {
                tagLabelPages = tagLabelSearchRepository
                        .findByTypeAndStatusAndHiddenOrderByLabelAsc(type,
                                ACTIVE, showHidden, pageable);
            }

            return tagLabelPages;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find a Tags by type", e);
        }
    }

    @Override
    public Page<TagLabel> findByLabelStartWithAndType(String labelStartwith,
            String tagType, boolean showHidden, Pageable pageable)
            throws ConsoleException {
        try {
            TagType type = TagType.getType(tagType);

            BoolQueryBuilder query = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchPhrasePrefixQuery("label",
                            labelStartwith))
                    .must(QueryBuilders.matchQuery("type", type.name()))
                    .must(QueryBuilders.matchQuery("status", ACTIVE.name()));

            if (!showHidden) {
                query.must(QueryBuilders.termQuery("hidden", showHidden));
            }

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable).build();

            log.debug("Search query By Label starts with and Type:{} ", query);

            return tagLabelSearchRepository.search(searchQuery);

        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find a Tags by label starts with and type",
                    e);
        }
    }

    @Override
    public Page<TagLabel> findByLabelAndType(String label, String tagType,
            boolean showHidden, Pageable pageable) throws ConsoleException {
        try {
            TagType type = TagType.getType(tagType);

            BoolQueryBuilder query = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("label", label))
                    .must(QueryBuilders.termQuery("status", ACTIVE.name()))
                    .must(QueryBuilders.termQuery("type", type.name()));

            if (!showHidden) {
                query.must(QueryBuilders.termQuery("hidden", showHidden));
            }

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable).build();

            log.debug("Search query By Label and Type:{} ", query);

            return tagLabelSearchRepository.search(searchQuery);
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered while find a Tags by label and type", e);
        }
    }

    @Override
    public List<TagLabel> findByKey(String key, TagType tagType)
            throws ConsoleException {
        List<TagLabel> tagLabels = tagLabelDao.findByKey(key, tagType);

        log.debug("Tag Labels found by given key, [ Key: {}, Size: {}]", key,
                tagLabels.size());
        return tagLabels;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeTag(Long id) throws ConsoleException {
        try {

            TagLabel tagLabel = tagLabelDao.findById(id);
            if (tagLabel != null) {
                tagLabelSearchRepository.deleteById(id);
                tagLabelDao.delete(tagLabel);
            } else {
                throw new NoDataFoundException(
                        msgBundle.getText("no.entity.found.delete.code"),
                        msgBundle.getText("no.entity.found.delete",
                                "Tag Label"));
            }

        } catch (Exception e) {
            throw new ConsoleException(String.format(
                    "Error encountered while removing a Tags [ Tag Id : %s ]",
                    id), e);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void reIndexAllTags() throws ConsoleException {
        try {

            List<TagLabel> tagLabels = tagLabelDao.findAll();
            if (!tagLabels.isEmpty()) {
                tagLabelSearchRepository.deleteAll();

                for (TagLabel tagLabel : tagLabels) {
                    tagLabelSearchRepository.save(tagLabel);
                }

                log.info(
                        "Tag label re-indexing successfull, No of re-indexes :{}",
                        tagLabels.size());
            }

        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in Tag label reindexing", e);
        }
    }

}
