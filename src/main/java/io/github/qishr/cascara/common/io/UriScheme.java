package io.github.qishr.cascara.common.io;

import java.net.URI;

public enum UriScheme {
    UNKNOWN(null),
    CASCARA("cascara"),
    FILE("file"),
    RES("res"),
    HTTP("http"),
    HTTPS("https"),
    FTP("ftp"),
    SFTP("sftp");

    private final String string;

    UriScheme(String string) {
        this.string = string;
    }

    public String asString() { return string; }

    public static UriScheme of(URI uri) {
        if (uri == null) return UriScheme.UNKNOWN;
        String scheme = uri.getScheme();
        for (UriScheme candidate : UriScheme.values()) {
            if (scheme.equals(candidate.asString())) {
                return candidate;
            }
        }
        return UriScheme.UNKNOWN;
    }

    @Override
    public String toString() { return string; }
}
