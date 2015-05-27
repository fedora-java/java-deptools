package org.fedoraproject.javadeptools.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.fedoraproject.javadeptools.ClassEntry;
import org.fedoraproject.javadeptools.FileArtifact;

@Entity
@Table(name = "fileArtifact")
public class PersistentFileArtifact implements FileArtifact {

    private Set<PersistentClassEntry> classes;
    private Long id;
    private String path;
    private PersistentPackage pkg;

    public PersistentFileArtifact() {
    }

    public PersistentFileArtifact(String path) {
        this.path = path;
    }

    @OneToMany(mappedBy = "fileArtifact")
    public Collection<ClassEntry> getClasses() {
        return Collections.<ClassEntry>unmodifiableCollection(classes);
    }

    /* (non-Javadoc)
     * @see org.fedoraproject.javadeptools.impl.FileArtifact#getId()
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.fedoraproject.javadeptools.impl.FileArtifact#getPath()
     */
    public String getPath() {
        return path;
    }

    /* (non-Javadoc)
     * @see org.fedoraproject.javadeptools.impl.FileArtifact#getPkg()
     */
    @ManyToOne
    @JoinColumn(name = "pkgId")
    public PersistentPackage getPkg() {
        return pkg;
    }

    public void setClasses(Set<PersistentClassEntry> classes) {
        this.classes = classes;
    }
    
    public void addClass(PersistentClassEntry clazz) {
        this.classes.add(clazz);
    }

    /* (non-Javadoc)
     * @see org.fedoraproject.javadeptools.impl.FileArtifact#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.fedoraproject.javadeptools.impl.FileArtifact#setPath(java.lang.String)
     */
    public void setPath(String path) {
        this.path = path;
    }

    /* (non-Javadoc)
     * @see org.fedoraproject.javadeptools.impl.FileArtifact#setPkg(org.fedoraproject.javadeptools.Package)
     */
    public void setPkg(PersistentPackage pkg) {
        this.pkg = pkg;
    }
}
