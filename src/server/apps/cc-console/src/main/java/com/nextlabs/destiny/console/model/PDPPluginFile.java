package com.nextlabs.destiny.console.model;

import com.nextlabs.destiny.console.enums.PDPPluginFileType;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity for PDP plugin file.
 *
 * @author Chok Shah Neng
 * @since 2020.12
 */
@Entity
@Table(name = "PDP_PLUGIN_FILES")
public class PDPPluginFile {
    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="PLUGIN_ID", nullable=false)
    private PDPPlugin plugin;

    @Column(name = "NAME", length = 260, nullable = false)
    private String name;

    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private PDPPluginFileType type;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "CONTENT", nullable = false)
    private byte[] content;

    @Column(name = "MODIFIED_DATE")
    private Long modifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PDPPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(PDPPlugin plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PDPPluginFileType getType() {
        return type;
    }

    public void setType(PDPPluginFileType type) {
        this.type = type;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
