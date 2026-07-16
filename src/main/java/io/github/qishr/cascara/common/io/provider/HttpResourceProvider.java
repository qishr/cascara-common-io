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


package io.github.qishr.cascara.common.io.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;

import javax.net.ssl.SSLHandshakeException;

import io.github.qishr.cascara.common.content.type.ContentTypeStore;
import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.diagnostic.code.DnsDiagnosticCode;
import io.github.qishr.cascara.common.diagnostic.code.GenericDiagnosticCode;
import io.github.qishr.cascara.common.diagnostic.code.InetDiagnosticCode;
import io.github.qishr.cascara.common.io.ResourceStream;
import io.github.qishr.cascara.common.io.UriScheme;
import io.github.qishr.cascara.common.util.ContentType;

public class HttpResourceProvider extends AbstractResourceProvider {

    public HttpResourceProvider() {
        super(UriScheme.HTTP);
    }

    @Override
    public ResourceStream getResourceAsStream(URI uri) throws LocalizableIOException {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                throw new IOException("HTTP " + response.statusCode());
            }

            String mime = response.headers()
                    .firstValue("Content-Type")
                    .orElse(null);

            ContentType contentType = ContentTypeStore.instance().resolve(mime);
            if (contentType == null) {
                contentType = new ContentType().withType(mime);
            }
            return new ResourceStream(response.body(), contentType);

        } catch (UnknownHostException e) {
            throw new LocalizableIOException(DnsDiagnosticCode.UNKNOWN_HOST, uri.getHost());
        } catch (ConnectException e) {
            throw new LocalizableIOException(InetDiagnosticCode.CONNECTION_REFUSED, uri.getHost());
        } catch (HttpTimeoutException e) {
            throw new LocalizableIOException(InetDiagnosticCode.CONNECTION_TIMEOUT, uri.getHost());
        } catch (SSLHandshakeException e) {
            throw new LocalizableIOException(InetDiagnosticCode.TLS_HANDSHAKE_FAILED, uri.getHost());
        } catch (IOException e) {
            throw new LocalizableIOException(GenericDiagnosticCode.IO_ERROR, e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LocalizableIOException(GenericDiagnosticCode.INTERRUPT_ERROR, e.getMessage(), e);
        }
    }
}
