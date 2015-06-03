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

package org.fedoraproject.javadeptools;

import java.io.File;
import java.util.Collection;

public interface Database {
    public Collection<Package> getPackages();
    public Package getPackage(String packageName);
    public Query<Package> queryPackages(String packageNameGlob);
    //public Collection<? extends FileArtifact> queryWhatRequires(String className);
    public Query<FileArtifact> queryFiles(String fileNameGlob);
    public Query<ClassEntry> queryClasses(String classNameGlob);
    public void build(Collection<File> path, boolean purge);
}
