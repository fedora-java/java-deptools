package org.fedoraproject.javadeptools.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table
public class ManifestEntry {
    @Id
    @GeneratedValue(generator = "gen")
    @GenericGenerator(name = "gen", strategy = "increment")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fileArtifactId", nullable = false)
    private FileArtifact fileArtifact;

    public ManifestEntry() {
    }

    public ManifestEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FileArtifact getFileArtifact() {
        return fileArtifact;
    }

    public void setFileArtifact(FileArtifact fileArtifact) {
        this.fileArtifact = fileArtifact;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String key;
    private String value;
}
