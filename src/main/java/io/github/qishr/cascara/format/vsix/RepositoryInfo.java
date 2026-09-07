package io.github.qishr.cascara.format.vsix;

public class RepositoryInfo {
    private String type;
    private String url;

    public RepositoryInfo() {
    }

    public String getType() {
        return type;
    }

    public RepositoryInfo setType(String s) {
        type = s;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public RepositoryInfo setUrl(String s) {
        url = s;
        return this;
    }
}
