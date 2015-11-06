package org.fedoraproject.javadeptools.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.fedoraproject.javadeptools.model.ClassEntry;
import org.fedoraproject.javadeptools.model.FileArtifact;
import org.fedoraproject.javadeptools.model.ManifestEntry;
import org.fedoraproject.javadeptools.model.Package;
import org.fedoraproject.javadeptools.rpm.RpmArchiveInputStream;

public class PackageProcessor {
    Package processPackage(File rpm) {
        String name = rpm.getName().replaceFirst("\\.rpm$", "")
                .replaceAll("-[^-]*-[^-]*$", "");
        Package pkg = new Package(name);
        try (ArchiveInputStream is = new RpmArchiveInputStream(rpm.toPath())) {
            CpioArchiveEntry entry;
            while ((entry = (CpioArchiveEntry) is.getNextEntry()) != null) {
                if (entry.isRegularFile() && entry.getName().endsWith(".jar")) {
                    String jarName = entry.getName().replaceFirst("^\\.", "");
                    FileArtifact fileArtifact = new FileArtifact(jarName);
                    // Getting manifest via JarInputStream is buggy, using Zip
                    // instead
                    ZipInputStream jarIs = new ZipInputStream(is);
                    processJar(jarIs, fileArtifact);
                    pkg.addFileArtifact(fileArtifact);
                }
            }
        } catch (IOException e) {
            // TODO
        }
        return pkg;
    }

    private void processJar(ZipInputStream is, FileArtifact fileArtifact)
            throws IOException {
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                processManifest(new Manifest(is), fileArtifact);
            } else if (!entry.isDirectory()
                    && entry.getName().endsWith(".class")
                    && !entry.getName().contains("$")) {
                processClass(entry, fileArtifact);
            }
        }
    }

    private void processClass(ZipEntry entry, FileArtifact fileArtifact) {
        String[] nameParts = entry.getName().replaceFirst("\\.class$", "")
                .split("/");
        StringBuilder packageNameBuilder = new StringBuilder();
        for (int i = 0; i < nameParts.length - 1; i++) {
            packageNameBuilder.append(nameParts[i]).append('.');
        }
        if (nameParts.length > 1) {
            packageNameBuilder.deleteCharAt(packageNameBuilder.length() - 1);
        }
        String packageName = packageNameBuilder.toString();
        String className = nameParts[nameParts.length - 1];
        ClassEntry classEntry = new ClassEntry(packageName, className);
        fileArtifact.addClass(classEntry);
    }

    private void processManifest(Manifest manifest, FileArtifact fileArtifact)
            throws IOException {
        if (manifest != null) {
            for (Map.Entry<Object, Object> entry : manifest.getMainAttributes()
                    .entrySet()) {
                fileArtifact.addManifestEntry(new ManifestEntry(entry.getKey()
                        .toString(), entry.getValue().toString()));
            }
        }
    }
}