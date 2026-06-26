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

import io.github.qishr.cascara.common.diagnostic.LocalizableException;
import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.diagnostic.code.DnsDiagnosticCode;
import io.github.qishr.cascara.common.diagnostic.code.GenericDiagnosticCode;
import io.github.qishr.cascara.common.diagnostic.code.InetDiagnosticCode;
import io.github.qishr.cascara.common.io.ResourceStream;
import io.github.qishr.cascara.common.io.UriScheme;

public class HttpResourceProvider extends AbstractResourceProvider {

    public HttpResourceProvider() {
        super(UriScheme.HTTP);
    }

    @Override
    public ResourceStream getResourceAsStream(URI uri) throws LocalizableException {
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

            return new ResourceStream(response.body(), mime);

        } catch (UnknownHostException e) {
            throw new LocalizableException(DnsDiagnosticCode.UNKNOWN_HOST, uri.getHost());
        } catch (ConnectException e) {
            throw new LocalizableException(InetDiagnosticCode.CONNECTION_REFUSED, uri.getHost());
        } catch (HttpTimeoutException e) {
            throw new LocalizableException(InetDiagnosticCode.CONNECTION_TIMEOUT, uri.getHost());
        } catch (SSLHandshakeException e) {
            throw new LocalizableException(InetDiagnosticCode.TLS_HANDSHAKE_FAILED, uri.getHost());
        } catch (IOException e) {
            throw new LocalizableIOException(GenericDiagnosticCode.IO_ERROR, e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LocalizableException(GenericDiagnosticCode.INTERRUPT_ERROR, e.getMessage(), e);
        }
    }
}
