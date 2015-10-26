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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(indexes = { @Index(name = "classEntry_fileArtifactId", columnList = "fileArtifactId") })
public class ClassEntry {

    private String className;
    private String packageName;

    @ManyToOne
    @JoinColumn(name = "fileArtifactId", nullable = false)
    private FileArtifact fileArtifact;

    @Id
    @GeneratedValue(generator = "gen")
    @GenericGenerator(name = "gen", strategy = "increment")
    private Long id;

    public ClassEntry() {
    }

    public ClassEntry(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public FileArtifact getFileArtifact() {
        return fileArtifact;
    }

    public Long getId() {
        return id;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setFileArtifact(FileArtifact fileArtifact) {
        this.fileArtifact = fileArtifact;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
