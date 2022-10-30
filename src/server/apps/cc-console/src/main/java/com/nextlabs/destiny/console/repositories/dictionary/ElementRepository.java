/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 6, 2020
 *
 */
package com.nextlabs.destiny.console.repositories.dictionary;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nextlabs.destiny.console.model.dictionary.Element;
import com.nextlabs.destiny.console.model.dictionary.Enrollment;
import com.nextlabs.destiny.console.model.dictionary.EnumGroup;
import com.nextlabs.destiny.console.model.dictionary.EnumGroupMember;
import com.nextlabs.destiny.console.model.dictionary.EnumMember;
import com.nextlabs.destiny.console.model.dictionary.LeafElement;
import com.nextlabs.destiny.console.model.dictionary.StructGroup;

public interface ElementRepository extends JpaRepository<Element, Long>, JpaSpecificationExecutor<Element> {

    List<Element> findByEnrollmentIdAndActiveToGreaterThan(Long id,
            long currentTimeMillis);

    Optional<Element> findByIdAndEnrollmentIdAndActiveToGreaterThan(Long id, Long enrollmentId,
            long currentTimeMillis);

    default Page<Element> findByCriteria(List<Long> enrollmentIds, List<Long> types, String text, Long group, Pageable pageable) {
        long currentTime = new Date().getTime();
        return findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("activeFrom"), currentTime),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("activeTo"), currentTime)));

            Subquery<StructGroup> structGroupsSubQuery = criteriaBuilder.createQuery().subquery(StructGroup.class);
            Root<StructGroup> structGroupRoot = structGroupsSubQuery.from(StructGroup.class);
            structGroupsSubQuery.select(structGroupRoot.get("elementId"));
            predicates.add(criteriaBuilder.not(criteriaBuilder.in(root.get("id")).value(structGroupsSubQuery)));

            if (types != null && !types.isEmpty()) {
                Join<Element, LeafElement> leafElementJoin = root.join("leafElement", JoinType.LEFT);
                if (types.contains((long) -1)) {
                    Join<Element, EnumGroup> groupJoin = root.join("group", JoinType.LEFT);
                    predicates.add(criteriaBuilder.or(groupJoin.get("elementId").isNotNull(),
                            leafElementJoin.get("typeId").in(types)));
                } else {
                    predicates.add(leafElementJoin.get("typeId").in(types));
                }
            }
            if (enrollmentIds != null && !enrollmentIds.isEmpty()) {
                Join<Element, Enrollment> enrollmentJoin = root.join("enrollment", JoinType.LEFT);
                predicates.add(enrollmentJoin.get("id").in(enrollmentIds));
            }
            if (StringUtils.isNotEmpty(text)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.upper(root.get("uniqueName")), String.format("%%%s%%", text.toUpperCase())),
                        criteriaBuilder.like(criteriaBuilder.upper(root.get("displayName")), String.format("%%%s%%", text.toUpperCase()))));
            }
            if (group != null && group >= 0) {
                Subquery<EnumMember> memberGroupsSubQuery = criteriaBuilder.createQuery().subquery(EnumMember.class);
                Root<EnumMember> enumMemberRoot = memberGroupsSubQuery.from(EnumMember.class);
                memberGroupsSubQuery
                        .select(enumMemberRoot.get("memberId"))
                        .where(criteriaBuilder.and(criteriaBuilder.equal(enumMemberRoot.get("groupId"), group),
                                criteriaBuilder.lessThanOrEqualTo(root.get("activeFrom"), currentTime),
                                criteriaBuilder.greaterThanOrEqualTo(root.get("activeTo"), currentTime)));

                Subquery<EnumGroupMember> groupsSubQuery = criteriaBuilder.createQuery().subquery(EnumGroupMember.class);
                Root<EnumGroupMember> enumGroupRoot = groupsSubQuery.from(EnumGroupMember.class);
                groupsSubQuery
                        .select(enumGroupRoot.get("fromId"))
                        .where(criteriaBuilder.and(criteriaBuilder.equal(enumGroupRoot.get("toId"), group),
                                criteriaBuilder.lessThanOrEqualTo(root.get("activeFrom"), currentTime),
                                criteriaBuilder.greaterThanOrEqualTo(root.get("activeTo"), currentTime)));

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.in(root.get("id")).value(memberGroupsSubQuery),
                        criteriaBuilder.in(root.get("id")).value(groupsSubQuery)
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

}
