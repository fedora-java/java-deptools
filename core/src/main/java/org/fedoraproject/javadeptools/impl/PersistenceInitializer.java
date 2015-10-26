package org.fedoraproject.javadeptools.impl;

import javax.inject.Inject;

import com.google.inject.persist.PersistService;

public class PersistenceInitializer {

    @Inject
    PersistenceInitializer(PersistService service) {
        service.start();
    }
}
