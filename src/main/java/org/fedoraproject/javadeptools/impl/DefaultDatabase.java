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

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.fedoraproject.javadeptools.ClassEntry;
import org.fedoraproject.javadeptools.Database;
import org.fedoraproject.javadeptools.FileArtifact;
import org.fedoraproject.javadeptools.Package;

public class DefaultDatabase implements Database {

    private EntityManager em;

    public DefaultDatabase(EntityManager em) {
        this.em = em;
    }

    public Collection<Package> getPackages() {
        List<Package> result = em.createQuery("from PersistentPackage",
                Package.class).getResultList();
        return result;
    }

    public Package getPackage(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Package> queryPackages(String packageNameGlob) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<FileArtifact> queryFiles(String fileNameGlob) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<ClassEntry> queryClasses(String classNameGlob) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void build(File path) {
        DefaultDatabaseBuilder builder = new DefaultDatabaseBuilder(em);
        builder.build(path);
    }
}
