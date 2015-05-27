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
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.fedoraproject.javadeptools.Database;

public class DatabaseFactory {
    private EntityManager createEntityManager(String url) {
        HashMap<String, String> props = new HashMap<>();
        props.put("javax.persistence.jdbc.url", url);
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(
                "org.fedoraproject.javadeptools", props);
        return factory.createEntityManager();
    }

    public Database createDatabase(String url) {
        return new DefaultDatabase(createEntityManager(url));
    }

    public Database createDatabase(File file) {
        return createDatabase("jdbc:h2:file:" + file.getAbsolutePath());
    }
}
