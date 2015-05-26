package org.fedoraproject.javadeptools.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Package {
    private Long id;
    private String name;
    private Set<FileArtifact> fileArtifacts = new HashSet<FileArtifact>();

    @OneToMany(mappedBy = "pkg")
    public Set<FileArtifact> getFileArtifacts() {
        return fileArtifacts;
    }

    public void setFileArtifacts(Set<FileArtifact> fileArtifacts) {
        this.fileArtifacts = fileArtifacts;
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

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }
}
