package io.github.qishr.cascara.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import io.github.qishr.cascara.common.util.ContentType;
import io.github.qishr.cascara.common.content.ResourceContent;
import io.github.qishr.cascara.common.content.type.ContentTypeStore;
import io.github.qishr.cascara.common.diagnostic.LocalizableException;
import io.github.qishr.cascara.common.diagnostic.code.GenericDiagnosticCode;
import io.github.qishr.cascara.common.io.provider.CascaraResourceProvider;
import io.github.qishr.cascara.common.io.provider.FileResourceProvider;
import io.github.qishr.cascara.common.io.provider.HttpResourceProvider;
import io.github.qishr.cascara.common.io.provider.ResourceProvider;

public class IOUtils {

    private static final Map<UriScheme, ResourceProvider> providers = new HashMap<>();

    static {
        providers.put(UriScheme.CASCARA, new CascaraResourceProvider());
        providers.put(UriScheme.FILE, new FileResourceProvider());
        providers.put(UriScheme.HTTP, new HttpResourceProvider());
        providers.put(UriScheme.HTTPS, new HttpResourceProvider());
        // Don't register UriScheme.RES here.
        // Let the application register its own one so we can access its resources.
    }

    public static void setResourceProvider(UriScheme uriScheme, ResourceProvider provider) {
        providers.put(uriScheme, provider);
    }

    public static InputStream getContentAsStream(URI uri) throws LocalizableException {
        return getResourceAsStream(uri).stream;
    }

    public static ResourceContent getResource(URI uri) throws LocalizableException {
        ResourceStream res = getResourceAsStream(uri);
        String content;
		try {
			content = new String(res.stream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
            // TODO: i18n
            throw new LocalizableException(e, GenericDiagnosticCode.IO_ERROR, "Failed to read resource: " + e.getMessage());
		}
        ContentType ct = ContentTypeStore.instance().resolve(res.mimeType);
        if (ct == null && res.mimeType != null) {
            ct = new ContentType().withType(res.mimeType);
        }
        return new ResourceContent(content, ct);
    }

    public static ResourceStream getResourceAsStream(URI uri) throws LocalizableException {
        UriScheme scheme = UriScheme.of(uri);
        if (scheme == null || scheme == UriScheme.UNKNOWN) {
            throw new LocalizableException(GenericDiagnosticCode.UNKNOWN_URI_SCHEME, uri);
        }

        ResourceProvider provider = providers.get(scheme);
        if (provider == null) {
            throw new LocalizableException(GenericDiagnosticCode.NO_RESOURCE_PROVIDER, uri);
        }

        return provider.getResourceAsStream(uri);
    }
}
