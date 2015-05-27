package org.fedoraproject.javadeptools;

import java.util.Collection;

public interface FileArtifact {

    public abstract Package getPkg();

    public abstract String getPath();

    public abstract Collection<ClassEntry> getClasses();

}