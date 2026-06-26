package io.github.qishr.cascara.common.io.provider;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import io.github.qishr.cascara.common.diagnostic.LocalizableException;
import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.diagnostic.code.FileDiagnosticCode;
import io.github.qishr.cascara.common.diagnostic.code.GenericDiagnosticCode;
import io.github.qishr.cascara.common.io.ResourceStream;
import io.github.qishr.cascara.common.io.UriScheme;

public class FileResourceProvider extends AbstractResourceProvider {

    public FileResourceProvider() {
        super(UriScheme.FILE);
    }

    @Override
    public ResourceStream getResourceAsStream(URI uri) throws LocalizableException {
        try {
            Path path = Path.of(uri);
            return new ResourceStream(Files.newInputStream(path), null);
        } catch (IllegalArgumentException e) {
            throw new LocalizableException(e, GenericDiagnosticCode.INVALID_URI, uri.toString());
        } catch (NoSuchFileException e) {
            throw new LocalizableIOException(e, FileDiagnosticCode.FILE_NOT_FOUND, uri.toString());
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage() + ": " + uri.toString());
        }
    }

}
