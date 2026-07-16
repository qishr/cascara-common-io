package io.github.qishr.cascara.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.diagnostic.code.GenericDiagnosticCode;
import io.github.qishr.cascara.common.util.ContentType;

public final class ResourceStream extends InputStream {
    public final InputStream stream;
    public ContentType contentType;

    public ResourceStream(InputStream stream, ContentType contentType) {
        this.stream = stream;
        this.contentType = contentType;
    }

    public ResourceStream setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public void close() throws IOException {
        try {
            stream.close();
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }
    }

    //
    // InputStream wrapper
    //

    public int read(byte[] b) throws IOException {
        try {
            return stream.read(b, 0, b.length);
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        try {
            return stream.read(b, off, len);
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }
    }

    public byte[] readAllBytes() throws IOException {
        try {
            return stream.readNBytes(Integer.MAX_VALUE);
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }
    }

    public byte[] readNBytes(int len) throws IOException {
        try {
            return stream.readNBytes(len);
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }
    }

    public int readNBytes(byte[] b, int off, int len) throws IOException {
        try {
            return stream.read(b, off, len);
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }
    }

    public long skip(long n) throws IOException {
        try {
            return stream.skip(n);
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }
    }

    public void skipNBytes(long n) throws IOException {
        try {
            stream.skipNBytes(n);
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }
    }

    public int available() throws IOException {
        try {
            return stream.available();
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }
    }

    public long transferTo(OutputStream out) throws IOException {
        try {
            return stream.transferTo(out);
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }
    }

	@Override
	public int read() throws IOException {
        try {
            return stream.read();
        } catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
        }
	}
}
