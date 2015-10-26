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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.fedoraproject.javadeptools.data.PackageCollectionDao;
import org.fedoraproject.javadeptools.data.PackageDao;
import org.fedoraproject.javadeptools.model.Package;
import org.fedoraproject.javadeptools.model.PackageCollection;

public class DefaultDatabaseBuilder {
    @Inject
    private PackageCollectionDao collectionDao;
    @Inject
    private PackageDao packageDao;

    private final static Logger logger = Logger
            .getLogger(DefaultDatabaseBuilder.class.getName());

    public void build(Collection<File> paths, String collectionName) {
        logger.info("Building from paths: " + paths);
        List<File> rpms = new ArrayList<>();
        paths.forEach(path -> findRpms(path, rpms));
        PackageCollection packageCollection = new PackageCollection(
                collectionName);
        collectionDao.startNewCollection(packageCollection);
        rpms.parallelStream().forEach(rpm -> {
                Package pkg = new PackageProcessor()
                        .processPackage(rpm);
                pkg.setPackageCollection(packageCollection);
                packageDao.persist(pkg);
            });
        collectionDao.finalizeCollection(packageCollection);
    }

    private void findRpms(File path, List<File> rpms) {
        if (path.isDirectory()) {
            // TODO error handling
            File[] fileList = path.listFiles();
            for (File file : fileList) {
                findRpms(file, rpms);
            }
        } else if (path.getName().endsWith(".rpm")) {
            rpms.add(path);
        }
    }
}
