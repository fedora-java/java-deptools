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
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(indexes = { @Index(name = "package_packageCollectionId", columnList = "packageCollectionId") })
public class Package {

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<FileArtifact> fileArtifacts = new HashSet<FileArtifact>();

    @Id
    @GeneratedValue(generator = "gen")
    @GenericGenerator(name = "gen", strategy = "increment")
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "packageCollectionId", nullable = false)
    private PackageCollection packageCollection;

    public PackageCollection getPackageCollection() {
        return packageCollection;
    }

    public void setPackageCollection(
            PackageCollection packageCollection) {
        this.packageCollection = packageCollection;
    }

    public Package() {
    }

    public Package(String name) {
        this.name = name;
    }

    public Collection<FileArtifact> getFileArtifacts() {
        return Collections.unmodifiableCollection(fileArtifacts);
    }

    public void addFileArtifact(FileArtifact file) {
        file.setPkg(this);
        this.fileArtifacts.add(file);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setFileArtifacts(Set<FileArtifact> fileArtifacts) {
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
