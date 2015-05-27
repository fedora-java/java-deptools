package org.fedoraproject.javadeptools.impl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.fedoraproject.javadeptools.ClassEntry;

@Entity
@Table(name = "classEntry")
public class PersistentClassEntry implements ClassEntry {
    private String className;
    private PersistentFileArtifact fileArtifact;
    private Long id;

    public PersistentClassEntry() {
    }

    public PersistentClassEntry(String className) {
        this.className = className;
    }

    /* (non-Javadoc)
     * @see org.fedoraproject.javadeptools.impl.ClassEntry#getClassName()
     */
    @Override
    public String getClassName() {
        return className;
    }

    /* (non-Javadoc)
     * @see org.fedoraproject.javadeptools.impl.ClassEntry#getFileArtifact()
     */
    @Override
    @ManyToOne
    @JoinColumn(name = "fileArtifactId")
    public PersistentFileArtifact getFileArtifact() {
        return fileArtifact;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setFileArtifact(PersistentFileArtifact fileArtifact) {
        this.fileArtifact = fileArtifact;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
