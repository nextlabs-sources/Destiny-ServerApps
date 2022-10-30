/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 15, 2016
 *
 */
package com.nextlabs.destiny.console.utils;

import static com.nextlabs.destiny.console.dto.common.SortField.ASC;
import static com.nextlabs.destiny.console.model.TagLabel.ALL_TAGS_KEY;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.Query;

import com.nextlabs.destiny.console.dto.common.DateFieldValue;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.SortField;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.common.TextSearchValue;
import com.nextlabs.destiny.console.enums.Operator;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.delegadmin.AccessibleTags;
import com.nextlabs.destiny.console.model.delegadmin.ApplicableTag;
import com.nextlabs.destiny.console.model.delegadmin.ObligationTag;

/**
 *
 * Utility to build the search criteria query based on the given criteria.
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public final class SearchCriteriaQueryBuilder {

    /**
     * Build a boolean query for given search fields
     *
     * @param searchFields
     * @return {@link BoolQueryBuilder}
     */
    @SuppressWarnings("unchecked")
    public static BoolQueryBuilder buildQuery(List<SearchField> searchFields) {

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (SearchField search : searchFields) {

            switch (search.getType()) {
                case SINGLE: {
                    StringFieldValue fieldValue = (StringFieldValue) search
                            .getValue();
                    query.must(QueryBuilders.matchPhrasePrefixQuery(
                            search.getField(), fieldValue.getValue()));
                    break;

                }
                case SINGLE_EXACT_MATCH: {
                    StringFieldValue fieldValue = (StringFieldValue) search
                            .getValue();
                    query.must(QueryBuilders.matchQuery(
                            search.getField(), fieldValue.getValue()));
                    break;

                }
                case MULTI: {
                    StringFieldValue fieldValue = (StringFieldValue) search
                            .getValue();

                    List<String> values = (List<String>) fieldValue.getValue();
                    if (values.size() == 1) {
                        query.must(QueryBuilders.matchPhrasePrefixQuery(
                                search.getField(), values.get(0)));
                    } else {
                        BoolQueryBuilder innerQuery = QueryBuilders.boolQuery();
                        for (String value : values) {
                            innerQuery.should(QueryBuilders
                                    .matchQuery(search.getField(), value));
                        }
                        query.must(innerQuery);
                    }

                    break;
                }
                case MULTI_EXACT_MATCH: {
                    StringFieldValue fieldValue = (StringFieldValue) search
                            .getValue();

                    List<String> values = (List<String>) fieldValue.getValue();
                    if (values.size() == 1) {
                        query.must(QueryBuilders.matchQuery(
                                search.getField(), values.get(0)));
                    } else {
                        BoolQueryBuilder innerQuery = QueryBuilders.boolQuery();
                        for (String value : values) {
                            innerQuery.should(QueryBuilders
                                    .matchQuery(search.getField(), value));
                        }
                        query.must(innerQuery);
                    }

                    break;
                }
                case NESTED: {
                    StringFieldValue fieldValue = (StringFieldValue) search
                            .getValue();
                    query.must(
                            QueryBuilders.nestedQuery(search.getField(),
                                    QueryBuilders.matchQuery(
                                            search.getNestedField(), fieldValue
                                                    .getValue()), ScoreMode.Avg));
                    break;
                }
                case NESTED_MULTI: {
                    StringFieldValue fieldValue = (StringFieldValue) search
                            .getValue();
                    List<String> values = (List<String>) fieldValue.getValue();

                    if (values.isEmpty()) {
                        // An empty array means "only match items with no values"
                        //
                        // This somewhat laborious query is complicated by the facts that
                        // * The version of elasticsearch we are using makes a distinction between
                        //   filters and queries and some methods that might be useful don't work
                        // * Due to a bug, missing nested filters don't work
                        query.must(QueryBuilders.matchAllQuery())
                                .mustNot(QueryBuilders.nestedQuery(search.getField(), QueryBuilders.matchAllQuery(),
                                        ScoreMode.Avg));
                    } else if (values.size() == 1) {
                        query.must(QueryBuilders.nestedQuery(search.getField(),
                                QueryBuilders.matchQuery(
                                        search.getNestedField(),
                                        values.get(0)), ScoreMode.Avg));
                    } else {
                        BoolQueryBuilder innerQuery = QueryBuilders.boolQuery();
                        for (String value : values) {
                            innerQuery.should(
                                    QueryBuilders.nestedQuery(search.getField(),
                                            QueryBuilders.matchQuery(
                                                    search.getNestedField(),
                                                    value), ScoreMode.Avg));
                        }
                        query.must(innerQuery);
                    }

                    break;
                }
                case DATE: {
                    DateFieldValue dateField = (DateFieldValue) search
                            .getValue();
                    query.must(QueryBuilders.rangeQuery(search.getField())
                            .from(dateField.getFromDate())
                            .to(dateField.getToDate()));
                    break;
                }
                case TEXT: {
                    TextSearchValue textSearch = (TextSearchValue) search
                            .getValue();
                    query.must(QueryBuilders
                            .multiMatchQuery(textSearch.getValue(),
                                    textSearch.getFields())
                            .type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX));
                    break;
                }

                default:
                    break;
            }
        }
        return query;
    }

    /**
     * Tag filter query for given accessible tags
     *
     * @param accessibleTags
     * @return {@link QueryBuilder}
     */
    public static QueryBuilder buildTagFilterQuery(
            AccessibleTags accessibleTags) {
        BoolQueryBuilder objTagsFilter = QueryBuilders.boolQuery();
        Long currentUserId = SecurityContextUtil.getCurrentUserId();

        if (accessibleTags == null || accessibleTags.getTags().isEmpty()) {
            return QueryBuilders.boolQuery()
                    .should(QueryBuilders.termQuery("createdDate", 0))
                    .should(QueryBuilders.termQuery("ownerId", currentUserId));
        }

        for (ObligationTag obligationTag : accessibleTags.getTags()) {

            BoolQueryBuilder andFilter = QueryBuilders.boolQuery();
            for (ApplicableTag applicableTag : obligationTag.getViewTags()) {
                if(applicableTag.getTags().stream().anyMatch(tagDTO -> !TagType.FOLDER_TAG.name().equals(tagDTO.getType()))) {
                    Operator operator = applicableTag.getOperator();
                    if (Operator.IN.equals(operator)) {
                        QueryBuilder tagValuesORFilter = addINTagsToORFilter(applicableTag);
                        if (tagValuesORFilter == null) {
                            tagValuesORFilter = QueryBuilders.matchAllQuery();
                            andFilter.must(tagValuesORFilter);
                        } else {
                            NestedQueryBuilder nestedFilter = QueryBuilders
                                    .nestedQuery("tags", tagValuesORFilter, ScoreMode.Avg);
                            andFilter.must(nestedFilter);
                        }
                    } else {
                        QueryBuilder tagValuesORFilter = addNOTTagsToORFilter(applicableTag);

                        NestedQueryBuilder nestedFilter = QueryBuilders
                                .nestedQuery("tags", tagValuesORFilter, ScoreMode.Avg);

                        BoolQueryBuilder boolFilter = QueryBuilders.boolQuery();
                        boolFilter.mustNot(nestedFilter);
                        andFilter.must(boolFilter);
                    }
                }
            }
            objTagsFilter.should(andFilter);
        }
        return objTagsFilter;
    }

    private static QueryBuilder addINTagsToORFilter(ApplicableTag applicableTag) {
        BoolQueryBuilder tagValuesORFilter = QueryBuilders.boolQuery();
        for (TagDTO tag : applicableTag.getTags().stream()
                .filter(tagDTO -> !TagType.FOLDER_TAG.name().equals(tagDTO.getType()))
                .collect(Collectors.toList())) {
            // skip filtering if all tags allowed
            if (tag.getKey().toLowerCase().equals(TagLabel.ALL_TAGS_KEY)) {
                return null;
            }
            tagValuesORFilter.should(QueryBuilders.termQuery("tags.key", tag.getKey().toLowerCase()));
        }
        return tagValuesORFilter;
    }

    private static QueryBuilder addNOTTagsToORFilter(
            ApplicableTag applicableTag) {
        BoolQueryBuilder tagValuesORFilter = QueryBuilders.boolQuery();
        for (TagDTO tag : applicableTag.getTags().stream()
                .filter(tagDTO -> !TagType.FOLDER_TAG.name().equals(tagDTO.getType()))
                .collect(Collectors.toList())) {
            // all tags are not allowed
            if (tag.getKey().toLowerCase().equals(TagLabel.ALL_TAGS_KEY)) {
                tagValuesORFilter.should(QueryBuilders.termQuery("tags.key", ALL_TAGS_KEY));
                return tagValuesORFilter;
            }
            tagValuesORFilter.should(QueryBuilders.termQuery("tags.key", tag.getKey().toLowerCase()));
        }
        return tagValuesORFilter;
    }

    /**
     * Folder filter query for given accessible tags
     *
     * @param accessibleTags    the accessible tags
     * @return {@link QueryBuilder}
     */
    public static QueryBuilder buildFolderFilterQuery(AccessibleTags accessibleTags) {
        BoolQueryBuilder objTagsFilter = QueryBuilders.boolQuery();
        if (accessibleTags == null || accessibleTags.getTags().isEmpty()) {
            return QueryBuilders.boolQuery().should(QueryBuilders.termQuery("folderId", Short.MIN_VALUE));
        }
        for (ObligationTag obligationTag : accessibleTags.getTags()) {
            BoolQueryBuilder andFilter = QueryBuilders.boolQuery();
            for (ApplicableTag applicableTag : obligationTag.getViewTags()) {
                if(applicableTag.getTags().stream().anyMatch(tagDTO -> TagType.FOLDER_TAG.name().equals(tagDTO.getType()))) {
                    Operator operator = applicableTag.getOperator();
                    if (Operator.IN.equals(operator)) {
                        QueryBuilder tagValuesORFilter = addINFoldersToORFilter(applicableTag);
                        if (tagValuesORFilter == null) {
                            tagValuesORFilter = QueryBuilders.matchAllQuery();
                        }
                        andFilter.must(tagValuesORFilter);
                    } else {
                        QueryBuilder tagValuesORFilter = addNOTFoldersToORFilter(applicableTag);
                        BoolQueryBuilder boolFilter = QueryBuilders.boolQuery();
                        boolFilter.mustNot(tagValuesORFilter);
                        andFilter.must(boolFilter);
                    }
                }
            }
            objTagsFilter.should(andFilter);
        }
        return objTagsFilter;
    }

    private static QueryBuilder addINFoldersToORFilter(ApplicableTag applicableTag) {
        BoolQueryBuilder tagValuesORFilter = QueryBuilders.boolQuery();
        for (TagDTO tag : applicableTag.getTags().stream()
                .filter(tagDTO -> TagType.FOLDER_TAG.name().equals(tagDTO.getType()))
                .collect(Collectors.toList())) {
            if (TagLabel.ALL_FOLDERS_KEY.equalsIgnoreCase(tag.getKey())) {
                return null;
            }
            tagValuesORFilter.should(QueryBuilders.termQuery("folderId", tag.getKey().toLowerCase()));
        }
        return tagValuesORFilter;
    }

    private static QueryBuilder addNOTFoldersToORFilter(
            ApplicableTag applicableTag) {
        BoolQueryBuilder tagValuesORFilter = QueryBuilders.boolQuery();
        for (TagDTO tag : applicableTag.getTags().stream()
                .filter(tagDTO -> TagType.FOLDER_TAG.name().equals(tagDTO.getType()))
                .collect(Collectors.toList())) {
            if (TagLabel.ALL_FOLDERS_KEY.equalsIgnoreCase(tag.getKey())) {
                tagValuesORFilter.should(QueryBuilders.termQuery("folderId", TagLabel.ALL_FOLDERS_KEY));
                return tagValuesORFilter;
            }
            tagValuesORFilter.should(QueryBuilders.termQuery("folderId", tag.getKey().toLowerCase()));
        }
        return tagValuesORFilter;
    }

    /**
     * Add Sort field to the given query
     *
     * @param query
     *            search query
     * @param sortField
     *            list of {@link SortField}
     * @return search query
     */
    public static Query withIdSort(Query query,
                                         SortField sortField) {
        Sort.Direction direction = sortField.getOrder().equals(ASC)
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        query.addSort(Sort.by(direction, sortField.getField()));
        return query;
    }

    /**
     * Add Sort fields to the given query
     *
     * @param query
     *            search query
     * @param sortFields
     *            list of {@link SortField}
     * @return search query
     */
    public static Query withSorts(Query query,
                                        List<SortField> sortFields) {
        for (SortField sortField : sortFields) {
            Sort.Direction direction = sortField.getOrder().equals(ASC)
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            query.addSort(Sort.by(direction, sortField.getField() + ".untouched"));
        }
        return query;
    }

    /**
     * Get query with ROW status, [ACTIVE, IN-ACTIVE, DELETED]
     *
     * @param statuses
     * @return {@link BoolQueryBuilder}
     */
    public static BoolQueryBuilder withStatuses(Status... statuses) {
        BoolQueryBuilder innerQuery = QueryBuilders.boolQuery();
        for (Status status : statuses) {
            innerQuery.should(QueryBuilders.matchQuery("status", status.name()));
        }

        return innerQuery;
    }

}
