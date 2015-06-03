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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.persistence.EntityManager;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;

public class DefaultDatabaseBuilder {
    private EntityManager em;

    public DefaultDatabaseBuilder(EntityManager em) {
        this.em = em;
    }

    public void build(Collection<File> paths, boolean purge) {
        List<File> rpms = new ArrayList<>();
        paths.forEach(path -> findRpms(path, rpms));
        em.getTransaction().begin();
        if (purge) {
            em.createQuery("delete from PersistentClassEntry").executeUpdate();
            em.createQuery("delete from PersistentFileArtifact").executeUpdate();
            em.createQuery("delete from PersistentPackage").executeUpdate();
        }
        rpms.forEach(this::addRpm);
        em.getTransaction().commit();
    }

    private void findRpms(File path, List<File> rpms) {
        if (path.isFile() && path.getName().endsWith(".rpm")) {
            rpms.add(path);
        } else if (path.isDirectory()) {
            // TODO error handling
            File[] fileList = path.listFiles();
            for (File file : fileList) {
                findRpms(file, rpms);
            }
        }
    }

    public void addRpm(File rpm) {
        String name = rpm.getName().replaceFirst("\\.rpm$", "")
                .replaceAll("-[^-]*-[^-]*$", "");
        PersistentPackage pkg = new PersistentPackage(name);
        try (ArchiveInputStream is = new RpmArchiveInputStream(rpm)) {
            ArchiveEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".jar")) {
                    JarInputStream jarIs = new JarInputStream(is);
                    PersistentFileArtifact jar = processJar(jarIs, entry
                            .getName().replaceFirst("^\\./", ""));
                    pkg.addFileArtifact(jar);
                }
            }
        } catch (IOException e) {
            // TODO handle
        }
        // TODO cascade
        // em.createQuery("delete from PersistentPackage where name = ?0")
        // .setParameter(0, name).executeUpdate();
        em.persist(pkg);
        em.flush();
    }

    private PersistentFileArtifact processJar(JarInputStream is, String jarName)
            throws IOException {
        JarEntry entry;
        PersistentFileArtifact fileArtifact = new PersistentFileArtifact(
                jarName);
        while ((entry = is.getNextJarEntry()) != null) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                PersistentClassEntry classEntry = new PersistentClassEntry(
                        entry.getName().replaceFirst("\\.class$", "")
                                .replaceAll("/", "."));
                fileArtifact.addClass(classEntry);
            }
        }
        return fileArtifact;
    }
}
