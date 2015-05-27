package org.fedoraproject.javadeptools.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.fedoraproject.javadeptools.FileArtifact;
import org.fedoraproject.javadeptools.Package;

@Entity
@Table(name = "package")
public class PersistentPackage implements Package {
    private Set<PersistentFileArtifact> fileArtifacts = new HashSet<PersistentFileArtifact>();

    private Long id;

    private String name;

    public PersistentPackage() {
    }

    public PersistentPackage(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fedoraproject.javadeptools.impl.Package#getFileArtifacts()
     */
    @OneToMany(mappedBy = "pkg")
    public Collection<FileArtifact> getFileArtifacts() {
        return Collections.<FileArtifact>unmodifiableCollection(fileArtifacts);
    }
    
    public void addFileArtifact(PersistentFileArtifact file) {
        this.fileArtifacts.add(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fedoraproject.javadeptools.impl.Package#getId()
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fedoraproject.javadeptools.impl.Package#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.fedoraproject.javadeptools.impl.Package#setFileArtifacts(java.util
     * .Set)
     */
    public void setFileArtifacts(Set<PersistentFileArtifact> fileArtifacts) {
        this.fileArtifacts = fileArtifacts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fedoraproject.javadeptools.impl.Package#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.fedoraproject.javadeptools.impl.Package#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }
}
