package org.fedoraproject.javadeptools.cli;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.fedoraproject.javadeptools.data.ClassEntryDao;
import org.fedoraproject.javadeptools.data.PackageCollectionDao;
import org.fedoraproject.javadeptools.data.PackageDao;
import org.fedoraproject.javadeptools.impl.DefaultDatabaseBuilder;
import org.fedoraproject.javadeptools.model.ClassEntry;
import org.fedoraproject.javadeptools.model.PackageCollection;

public class Commands {

    @Inject
    private PackageCollectionDao collectionDao;
    @Inject
    private PackageDao packageDao;
    @Inject
    private ClassEntryDao classDao;
    @Inject
    private DefaultDatabaseBuilder databaseBuilder;

    private void printClassEntry(ClassEntry c) {
        System.out.println(c.getFileArtifact().getPkg().getName() + " | "
                + c.getFileArtifact().getPath() + " | " + c.getClassName());
    }

    public void query(String pattern) {
        // TODO collection name
        PackageCollection collection = collectionDao
                .getCollectionByName("primary");
        classDao.queryClassEntriesByName(collection, pattern).getResults()
                .forEach(this::printClassEntry);
    }

    public void build(Collection<String> args) {
        // TODO collection name
        databaseBuilder.build(
                args.stream().map(File::new).collect(Collectors.toList()),
                "primary");
    }

    public void list() {
        // TODO NYI
        throw new UnsupportedOperationException();
    }
}
