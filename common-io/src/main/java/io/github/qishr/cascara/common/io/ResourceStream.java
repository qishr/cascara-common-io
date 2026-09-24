// # License & Terms
//
// This file is part of **Cascara**.
//
// **Cascara** is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
//
// ---
//
// ## Special Runtime Exception
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules,
// and to copy and distribute the resulting executable under terms of your
// choice, provided that you also meet, for each linked independent module,
// the terms and conditions of the license of that module.
//
// An independent module is a module which is not derived from or based on
// this library. If you modify this library, you may extend this exception
// to your version of the library, but you are not obligated to do so. If
// you do not wish to do so, delete this exception statement from your
// version.


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
