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
@Table(name = EnumRefMember.ENUM_REF_MEMBERS_TABLE)
public class EnumRefMember {
	public static final String ENUM_REF_MEMBERS_TABLE = "DICT_ENUM_REF_MEMBERS";
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Version
    private int version;
    
    @Column(name = "group_id")
    private Long groupId;
    
    @Column(name = "enrollment_id")
    private Long enrollmentId;
    
    private String path;

    @Column(name = "PATH_HASH")
    private int pathHash;

    
	public EnumRefMember(Long groupId, Long enrollmentId, String path, int pathHash) {
		super();
		this.groupId = groupId;
		this.enrollmentId = enrollmentId;
		this.path = path;
		this.pathHash = pathHash;
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

	public Long getEnrollmentId() {
		return enrollmentId;
	}

	public void setEnrollmentId(Long enrollmentId) {
		this.enrollmentId = enrollmentId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPathHash() {
		return pathHash;
	}

	public void setPathHash(int pathHash) {
		this.pathHash = pathHash;
	} 
    
    
}
