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
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.fedoraproject.javadeptools.FileArtifact;
import org.fedoraproject.javadeptools.Package;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "package")
public class PersistentPackage implements Package {

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    private Set<PersistentFileArtifact> fileArtifacts = new HashSet<PersistentFileArtifact>();

    @Id
    @GeneratedValue(generator = "gen")
    @GenericGenerator(name = "gen", strategy = "increment")
    private Long id;

    private String name;

    public PersistentPackage() {
    }

    public PersistentPackage(String name) {
        this.name = name;
    }

    public Collection<FileArtifact> getFileArtifacts() {
        return Collections.<FileArtifact> unmodifiableCollection(fileArtifacts);
    }

    public void addFileArtifact(PersistentFileArtifact file) {
        file.setPkg(this);
        this.fileArtifacts.add(file);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setFileArtifacts(Set<PersistentFileArtifact> fileArtifacts) {
        fileArtifacts.forEach(f -> f.setPkg(this));
        this.fileArtifacts = fileArtifacts;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
