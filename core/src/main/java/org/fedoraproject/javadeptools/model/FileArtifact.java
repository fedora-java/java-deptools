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
package org.fedoraproject.javadeptools.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(indexes = { @Index(name = "fileArtifact_pkgId", columnList = "pkgId") })
public class FileArtifact {

    @OneToMany(mappedBy = "fileArtifact", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderBy("packageName, className")
    private Set<ClassEntry> classes = new HashSet<>();

    @OneToMany(mappedBy = "fileArtifact", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ManifestEntry> manifestEntries = new HashSet<>();

    @Id
    @GeneratedValue(generator = "gen")
    @GenericGenerator(name = "gen", strategy = "increment")
    private Long id;

    private String path;

    private boolean valid = true;

    @ManyToOne
    @JoinColumn(name = "pkgId", nullable = false)
    private Package pkg;

    public FileArtifact() {
    }

    public FileArtifact(String path) {
        this.path = path;
    }

    public Collection<ClassEntry> getClasses() {
        return Collections.unmodifiableCollection(classes);
    }

    public Long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public Package getPkg() {
        return pkg;
    }

    public void setClasses(Set<ClassEntry> classes) {
        classes.forEach(c -> c.setFileArtifact(this));
        this.classes = classes;
    }

    public void addClass(ClassEntry clazz) {
        clazz.setFileArtifact(this);
        classes.add(clazz);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPkg(Package pkg) {
        this.pkg = pkg;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void addManifestEntry(ManifestEntry manifestEntry) {
        manifestEntry.setFileArtifact(this);
        manifestEntries.add(manifestEntry);
    }

    public Set<ManifestEntry> getManifestEntries() {
        return Collections.unmodifiableSet(manifestEntries);
    }

    public void setManifestEntries(Set<ManifestEntry> manifestEntries) {
        manifestEntries.forEach(e -> e.setFileArtifact(this));
        this.manifestEntries = manifestEntries;
    }
}
