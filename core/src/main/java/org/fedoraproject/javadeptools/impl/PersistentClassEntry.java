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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.fedoraproject.javadeptools.ClassEntry;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "classEntry")
public class PersistentClassEntry implements ClassEntry {

    private String className;
    private String packageName;

    @ManyToOne
    @JoinColumn(name = "fileArtifactId")
    private PersistentFileArtifact fileArtifact;

    @Id
    @GeneratedValue(generator = "gen")
    @GenericGenerator(name = "gen", strategy = "increment")
    private Long id;

    public PersistentClassEntry() {
    }

    public PersistentClassEntry(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public PersistentFileArtifact getFileArtifact() {
        return fileArtifact;
    }

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

    @Override
    public String getPackageName() {
        return packageName;
    }
}
