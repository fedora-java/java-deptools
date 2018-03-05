/*-
 * Copyright (c) 2012-2015 Red Hat, Inc.
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
package org.fedoraproject.javadeptools.rpm;

import static org.fedoraproject.javadeptools.rpm.Rpm.Fclose;
import static org.fedoraproject.javadeptools.rpm.Rpm.Ferror;
import static org.fedoraproject.javadeptools.rpm.Rpm.Fopen;
import static org.fedoraproject.javadeptools.rpm.Rpm.Fstrerror;
import static org.fedoraproject.javadeptools.rpm.Rpm.Ftell;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMRC_NOKEY;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMRC_NOTFOUND;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMRC_NOTTRUSTED;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMRC_OK;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMTAG_PAYLOADCOMPRESSOR;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMTAG_PAYLOADFORMAT;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMVSF_NODSA;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMVSF_NODSAHEADER;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMVSF_NOHDRCHK;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMVSF_NOMD5;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMVSF_NOMD5HEADER;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMVSF_NORSA;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMVSF_NORSAHEADER;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMVSF_NOSHA1;
import static org.fedoraproject.javadeptools.rpm.Rpm.RPMVSF_NOSHA1HEADER;
import static org.fedoraproject.javadeptools.rpm.Rpm.headerFree;
import static org.fedoraproject.javadeptools.rpm.Rpm.headerGetString;
import static org.fedoraproject.javadeptools.rpm.Rpm.rpmReadPackageFile;
import static org.fedoraproject.javadeptools.rpm.Rpm.rpmtsCreate;
import static org.fedoraproject.javadeptools.rpm.Rpm.rpmtsFree;
import static org.fedoraproject.javadeptools.rpm.Rpm.rpmtsSetVSFlags;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

/**
 * A class for reading RPM package as an archive.
 * 
 * @author Mikolaj Izdebski
 */
public class RpmArchiveInputStream extends ArchiveInputStream {
    private final ArchiveInputStream delegate;

    public RpmArchiveInputStream(Path path) throws IOException {
        this.delegate = wrapFile(path);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public ArchiveEntry getNextEntry() throws IOException {
        return delegate.getNextEntry();
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }

    @Override
    public int read(byte[] buf) throws IOException {
        return delegate.read(buf);
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        return delegate.read(buf, off, len);
    }

    private static IOException error(Path path, String message) throws IOException {
        throw new IOException("Unable to open RPM file " + path + ": " + message);
    }

    private static boolean hasGzipMagic(InputStream fis) throws IOException {
        try {
            fis.mark(2);
            return fis.read() == 31 && fis.read() == 139;
        } finally {
            fis.reset();
        }
    }

    private static ArchiveInputStream wrapFile(Path path) throws IOException {
        String archiveFormat;
        String compressionMethod;
        long offset;
        Pointer ts = rpmtsCreate();
        Pointer fd = Fopen(path.toString(), "r");
        try {
            if (Ferror(fd))
                throw error(path, Fstrerror(fd));
            rpmtsSetVSFlags(ts, RPMVSF_NOHDRCHK | RPMVSF_NOSHA1HEADER | RPMVSF_NOMD5HEADER | RPMVSF_NODSAHEADER
                    | RPMVSF_NORSAHEADER | RPMVSF_NOSHA1 | RPMVSF_NOMD5 | RPMVSF_NODSA | RPMVSF_NORSA);
            Pointer ph = new Memory(Pointer.SIZE);
            int rc = rpmReadPackageFile(ts, fd, null, ph);
            if (rc == RPMRC_NOTFOUND)
                throw error(path, "Not a RPM file");
            if (rc != RPMRC_OK && rc != RPMRC_NOTTRUSTED && rc != RPMRC_NOKEY)
                throw error(path, "Failed to parse RPM header");
            Pointer h = ph.getPointer(0);
            try {
                archiveFormat = headerGetString(h, RPMTAG_PAYLOADFORMAT);
                compressionMethod = headerGetString(h, RPMTAG_PAYLOADCOMPRESSOR);
            } finally {
                headerFree(h);
            }
            offset = Ftell(fd);
        } finally {
            Fclose(fd);
            rpmtsFree(ts);
        }

        InputStream fis = new BufferedInputStream(Files.newInputStream(path));
        fis.skip(offset);

        InputStream cis;
        if (compressionMethod == null)
            compressionMethod = "gzip";
        switch (compressionMethod) {
        case "gzip":
            if (hasGzipMagic(fis))
                cis = new GzipCompressorInputStream(fis, true);
            else
                cis = fis;
            break;
        case "bzip2":
            cis = new BZip2CompressorInputStream(fis);
            break;
        case "xz":
            cis = new XZCompressorInputStream(fis);
            break;
        case "lzma":
            cis = new LZMACompressorInputStream(fis);
            break;
        default:
            fis.close();
            throw error(path, "Unsupported compression method: " + compressionMethod);
        }

        ArchiveInputStream ais;
        if (archiveFormat == null)
            archiveFormat = "cpio";
        switch (archiveFormat) {
        case "cpio":
            ais = new CpioArchiveInputStream(cis);
            break;
        default:
            cis.close();
            throw error(path, "Unsupported archive format: " + archiveFormat);
        }

        return ais;
    }

}
