package com.nextlabs.destiny.configservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Entity for secure store.
 *
 * @author Sachindra Dasun
 */
@Entity
@Table(name = "SECURE_STORE")
public class SecureStore {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "STORE_FILE", nullable = false)
    private byte[] storeFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getStoreFile() {
        return storeFile;
    }

    public void setStoreFile(byte[] storeFile) {
        this.storeFile = storeFile;
    }

}
