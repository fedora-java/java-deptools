package org.fedoraproject.javadeptools.data;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.fedoraproject.javadeptools.model.FileArtifact;

public class FileArtifactDao {

    @Inject
    private EntityManager em;

    public FileArtifact findById(Long id) {
        return em.find(FileArtifact.class, id);
    }
}
