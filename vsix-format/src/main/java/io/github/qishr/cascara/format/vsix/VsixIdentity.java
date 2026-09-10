package io.github.qishr.cascara.format.vsix;

import java.util.UUID;

public class VsixIdentity {
    private static final String MODULE_NAME = "cascara.format.vsix";

	private String language = "en-US";
    private String id;
    private String version;
    private String publisher;

    public VsixIdentity() {
		setId(null);
	}

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
		if (id == null) {
			id = MODULE_NAME + "." + UUID.randomUUID();
		}
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
