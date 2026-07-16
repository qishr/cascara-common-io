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
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import io.github.qishr.cascara.common.content.ResourceContent;
import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
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

    public static InputStream getContentAsStream(URI uri) throws LocalizableIOException {
        return getResourceAsStream(uri).stream;
    }

    public static ResourceContent getResource(URI uri) throws LocalizableIOException {
        ResourceStream res = getResourceAsStream(uri);
        String content;
		try {
			content = new String(res.stream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
            // TODO: i18n
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, "Failed to read resource: " + e.getMessage());
		}
        return new ResourceContent(content, res.contentType);
    }

    public static ResourceStream getResourceAsStream(URI uri) throws LocalizableIOException {
        UriScheme scheme = UriScheme.of(uri);
        if (scheme == null || scheme == UriScheme.UNKNOWN) {
            throw new LocalizableIOException(GenericDiagnosticCode.UNKNOWN_URI_SCHEME, uri);
        }
        ResourceProvider provider = providers.get(scheme);
        if (provider == null) {
            throw new LocalizableIOException(GenericDiagnosticCode.NO_RESOURCE_PROVIDER, uri);
        }
        return provider.getResourceAsStream(uri);
    }

    //
    // Convenience methods
    //

    public static InputStream getContentAsStream(String uri) throws LocalizableIOException {
        return getContentAsStream(URI.create(uri));
    }

    public static ResourceContent getResource(String uri) throws LocalizableIOException {
        return getResource(URI.create(uri));
    }

    public static ResourceStream getResourceAsStream(String uri) throws LocalizableIOException {
        return getResourceAsStream(URI.create(uri));
    }
}
