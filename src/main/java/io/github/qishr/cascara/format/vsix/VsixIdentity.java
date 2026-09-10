package io.github.qishr.cascara.format.vsix;

public class VsixIdentity {
    private String language = "en-US";
    private String id;
    private String version;
    private String publisher;

    public VsixIdentity() {}

	public String getLanguage() {
		return language;
	}

	public VsixIdentity setLanguage(String language) {
		this.language = language;
        return this;
	}

	public String getId() {
		return id;
	}

	public VsixIdentity setId(String id) {
		this.id = id;
        return this;
	}

	public String getVersion() {
		return version;
	}

	public VsixIdentity setVersion(String version) {
		this.version = version;
        return this;
	}

	public String getPublisher() {
		return publisher;
	}

	public VsixIdentity setPublisher(String publisher) {
		this.publisher = publisher;
        return this;
	}
}
