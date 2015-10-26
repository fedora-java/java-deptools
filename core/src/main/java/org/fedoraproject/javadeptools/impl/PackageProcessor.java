package org.fedoraproject.javadeptools.impl;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.fedoraproject.javadeptools.model.ClassEntry;
import org.fedoraproject.javadeptools.model.FileArtifact;
import org.fedoraproject.javadeptools.model.Package;
import org.fedoraproject.javadeptools.rpm.RpmArchiveInputStream;

public class PackageProcessor {
    Package processPackage(File rpm) {
        String name = rpm.getName().replaceFirst("\\.rpm$", "")
                .replaceAll("-[^-]*-[^-]*$", "");
        Package pkg = new Package(name);
        try (ArchiveInputStream is = new RpmArchiveInputStream(rpm.toPath())) {
            ArchiveEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".jar")) {
                    try {
                        JarInputStream jarIs = new JarInputStream(is);
                        FileArtifact jar = processJar(jarIs, entry.getName()
                                .replaceFirst("^\\.", ""));
                        pkg.addFileArtifact(jar);

                        // JAR processing throws SecurityException on invalid
                        // manifests
                    } catch (SecurityException e) {
                        // TODO
                    }
                }
            }
        } catch (IOException e) {
            // TODO
        }
        return pkg;
    }

    private FileArtifact processJar(JarInputStream is, String jarName)
            throws IOException {
        JarEntry entry;
        FileArtifact fileArtifact = new FileArtifact(
                jarName);
        while ((entry = is.getNextJarEntry()) != null) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")
                    && !entry.getName().contains("$")) {
                String[] nameParts = entry.getName()
                        .replaceFirst("\\.class$", "").split("/");
                StringBuilder packageNameBuilder = new StringBuilder();
                for (int i = 0; i < nameParts.length - 1; i++) {
                    packageNameBuilder.append(nameParts[i]).append('.');
                }
                if (nameParts.length > 1) {
                    packageNameBuilder
                            .deleteCharAt(packageNameBuilder.length() - 1);
                }
                String packageName = packageNameBuilder.toString();
                String className = nameParts[nameParts.length - 1];
                ClassEntry classEntry = new ClassEntry(
                        packageName, className);
                fileArtifact.addClass(classEntry);
            }
        }
        return fileArtifact;
    }
}