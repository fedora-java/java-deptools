package org.fedoraproject.javadeptools;

import java.io.File;
import java.util.Collection;

public interface DatabaseBuilder {

    public void build(Collection<File> paths, String collectionName);

}