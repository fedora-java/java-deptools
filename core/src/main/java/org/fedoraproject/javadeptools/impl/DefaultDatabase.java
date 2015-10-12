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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.fedoraproject.javadeptools.ClassEntry;
import org.fedoraproject.javadeptools.Database;
import org.fedoraproject.javadeptools.FileArtifact;
import org.fedoraproject.javadeptools.Package;
import org.fedoraproject.javadeptools.Query;

public class DefaultDatabase implements Database {

    private EntityManager em;
    private EntityManagerFactory emf;

    public DefaultDatabase(EntityManagerFactory emf) {
        this.emf = emf;
        this.em = emf.createEntityManager();
    }

    @Override
    public Collection<Package> getPackages() {
        return em.createQuery("from PersistentPackage", Package.class)
                .getResultList();
    }

    @Override
    public Package getPackage(String name) {
        return em
                .createQuery("from PersistentPackage where name = ?0",
                        Package.class).setParameter(0, name).getSingleResult();
    }

    @Override
    public Query<Package> queryPackages(String packageNameGlob) {
        return new DefaultQuery<>(em, Package.class,
                "from PersistentPackage where name like ?0", packageNameGlob);
    }

    @Override
    public Query<FileArtifact> queryFiles(String fileNameGlob) {
        return new DefaultQuery<>(em, FileArtifact.class,
                "from PersistentFileArtifact where path like ?0", fileNameGlob);
    }

    @Override
    public Query<ClassEntry> queryClasses(String classNameGlob) {
        return new DefaultQuery<>(em, ClassEntry.class,
                "from PersistentClassEntry where concat(packageName, '.', className) like ?0",
                classNameGlob);
    }

    @Override
    public void build(Collection<File> paths, boolean purge) {
        DefaultDatabaseBuilder builder = new DefaultDatabaseBuilder(emf);
        builder.build(paths, purge);
    }
}
