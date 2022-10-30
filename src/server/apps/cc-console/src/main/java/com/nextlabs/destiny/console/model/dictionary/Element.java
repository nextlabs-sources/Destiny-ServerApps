/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 6, 2020
 *
 */
package com.nextlabs.destiny.console.model.dictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = Element.ELEMENT_TABLE)
public class Element {
    public static final String ELEMENT_TABLE = "DICT_ELEMENTS";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    private int version;

    @Column(name = "ORIGINAL_ID")
    private Long originalId;

    @Column(name = "DICTIONARY_KEY")
    private String dictionaryKey;

    @Column(name = "UNIQUE_NAME")
    private String uniqueName;

    private String displayName;

    private String path;

    @Column(name = "PATH_HASH")
    private int pathHash;

    private char reParented;

    @Column(name = "ACTIVE_FROM")
    private Long activeFrom;

    @Column(name = "ACTIVE_TO")
    private Long activeTo;

    @OneToOne
    @PrimaryKeyJoinColumn
    private EnumGroup group;

    @OneToOne
    @PrimaryKeyJoinColumn
    private LeafElement leafElement;

    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    public Element() {

    }

    public Element(Enrollment enrollment, String dictionaryKey, String uniqueName, String displayName,
            String path, int pathHash, char reParented, Long activeFrom, Long activeTo) {
        super();
        this.enrollment = enrollment;
        this.dictionaryKey = dictionaryKey;
        this.uniqueName = uniqueName;
        this.displayName = displayName;
        this.path = path;
        this.pathHash = pathHash;
        this.reParented = reParented;
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    public String getDictionaryKey() {
        return dictionaryKey;
    }

    public void setDictionaryKey(String dictionaryKey) {
        this.dictionaryKey = dictionaryKey;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public char getReParented() {
        return reParented;
    }

    public void setReParented(char reParented) {
        this.reParented = reParented;
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

    public LeafElement getLeafElement() {
        return leafElement;
    }

    public void setLeafElement(LeafElement leafElement) {
        this.leafElement = leafElement;
    }

    public EnumGroup getGroup() {
        return group;
    }

    public void setGroup(EnumGroup enumGroup) {
        this.group = enumGroup;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

}
