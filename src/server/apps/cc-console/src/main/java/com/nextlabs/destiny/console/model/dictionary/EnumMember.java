/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 *
 */
package com.nextlabs.destiny.console.model.dictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = EnumMember.ENUM_MEMBERS_TABLE)
public class EnumMember {
	public static final String ENUM_MEMBERS_TABLE = "DICT_ENUM_MEMBERS";
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Version
    private int version;
    
    @Column(name = "group_id")
    private Long groupId;
    
    @Column(name = "member_id")
    private Long memberId;
    
    @Column(name = "element_type_id")
    private Long elementTypeId;

    @Column(name = "enrollment_id")
    private Long enrollmentId;

    @Column(name = "active_from")
    private Long activeFrom;

    @Column(name = "active_to")
    private Long activeTo;

    
	public EnumMember(Long groupId, Long memberId, Long elementTypeId, Long enrollmentId, Long activeFrom,
			Long activeTo) {
		super();
		this.groupId = groupId;
		this.memberId = memberId;
		this.elementTypeId = elementTypeId;
		this.enrollmentId = enrollmentId;
		this.activeFrom = activeFrom;
		this.activeTo = activeTo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getElementTypeId() {
		return elementTypeId;
	}

	public void setElementTypeId(Long elementTypeId) {
		this.elementTypeId = elementTypeId;
	}

	public Long getEnrollmentId() {
		return enrollmentId;
	}

	public void setEnrollmentId(Long enrollmentId) {
		this.enrollmentId = enrollmentId;
	}

	public Long getActiveFrom() {
		return activeFrom;
	}

	public void setActiveFrom(Long activeFrom) {
		this.activeFrom = activeFrom;
	}

	public Long getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(Long activeTo) {
		this.activeTo = activeTo;
	}
}
