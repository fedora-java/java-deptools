/*-
 * Copyright (c) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fedoraproject.javadeptools.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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

    @OneToMany(mappedBy = "fileArtifact", cascade = CascadeType.ALL)
    private Set<PersistentClassEntry> classes = new HashSet<PersistentClassEntry>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String path;

    @ManyToOne
    @JoinColumn(name = "pkgId")
    private PersistentPackage pkg;

    public PersistentFileArtifact() {
    }

    public PersistentFileArtifact(String path) {
        this.path = path;
    }

    public Collection<ClassEntry> getClasses() {
        return Collections.<ClassEntry> unmodifiableCollection(classes);
    }

    public Long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public PersistentPackage getPkg() {
        return pkg;
    }

    public void setClasses(Set<PersistentClassEntry> classes) {
        classes.forEach(c -> c.setFileArtifact(this));
        this.classes = classes;
    }

    public void addClass(PersistentClassEntry clazz) {
        clazz.setFileArtifact(this);
        this.classes.add(clazz);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPkg(PersistentPackage pkg) {
        this.pkg = pkg;
    }
}
