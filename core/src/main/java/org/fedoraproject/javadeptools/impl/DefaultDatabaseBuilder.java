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

    public void build(File path) {
        List<File> rpms = new ArrayList<>();
        findRpms(path, rpms);
        for (File rpm : rpms) {
            addRpm(rpm);
        }
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
        em.getTransaction().begin();
        // TODO name
        PersistentPackage pkg = new PersistentPackage(rpm.getName());
        try (ArchiveInputStream is = new RpmArchiveInputStream(rpm)) {
            ArchiveEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".jar")) {
                    JarInputStream jarIs = new JarInputStream(is);
                    PersistentFileArtifact jar = processJar(jarIs,
                            entry.getName());
                    pkg.addFileArtifact(jar);
                }
            }
        } catch (IOException e) {
            // TODO handle
        }
        em.persist(pkg);
        em.getTransaction().commit();
    }

    private PersistentFileArtifact processJar(JarInputStream is, String jarName)
            throws IOException {
        JarEntry entry;
        PersistentFileArtifact fileArtifact = new PersistentFileArtifact(
                jarName);
        while ((entry = is.getNextJarEntry()) != null) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                PersistentClassEntry classEntry = new PersistentClassEntry(
                        entry.getName());
                fileArtifact.addClass(classEntry);
            }
        }
        return fileArtifact;
    }
}
