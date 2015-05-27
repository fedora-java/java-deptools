package org.fedoraproject.javadeptools;

import java.util.Collection;

public interface Package {

    public abstract Collection<FileArtifact> getFileArtifacts();

    public abstract String getName();

}