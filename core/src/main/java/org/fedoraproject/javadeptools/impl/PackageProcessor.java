package org.fedoraproject.javadeptools.impl;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.fedoraproject.javadeptools.rpm.RpmArchiveInputStream;

public class PackageProcessor {
    PersistentPackage processPackage(File rpm) {
        String name = rpm.getName().replaceFirst("\\.rpm$", "")
                .replaceAll("-[^-]*-[^-]*$", "");
        PersistentPackage pkg = new PersistentPackage(name);
        try (ArchiveInputStream is = new RpmArchiveInputStream(rpm.toPath())) {
            ArchiveEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".jar")) {
                    JarInputStream jarIs = new JarInputStream(is);
                    PersistentFileArtifact jar = processJar(jarIs, entry
                            .getName().replaceFirst("^\\./", ""));
                    pkg.addFileArtifact(jar);
                }
            }
        } catch (Exception e) {
            // TODO
        }
        return pkg;
    }

    private PersistentFileArtifact processJar(JarInputStream is, String jarName)
            throws IOException {
        JarEntry entry;
        PersistentFileArtifact fileArtifact = new PersistentFileArtifact(
                jarName);
        while ((entry = is.getNextJarEntry()) != null) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")
                    && !entry.getName().contains("$")) {
                PersistentClassEntry classEntry = new PersistentClassEntry(
                        entry.getName().replaceFirst("\\.class$", "")
                                .replaceAll("/", "."));
                fileArtifact.addClass(classEntry);
            }
        }
        return fileArtifact;
    }
}