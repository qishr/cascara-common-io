package io.github.qishr.cascara.format.vsix;

import io.github.qishr.cascara.common.util.Properties;

public class VsixThemeInfo {
    private Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    public String getLabel() {
        return properties.getString("label");
    }

    public String getUiTheme() {
        return properties.getString("uiTheme");
    }

    public String getPath() {
        String filePath = properties.getString("path");
        if (filePath.startsWith("./")) {
            filePath = filePath.substring(2);
        }
        return filePath;
    }
}
