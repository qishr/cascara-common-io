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

package io.github.qishr.cascara.format.vsix;

import io.github.qishr.cascara.common.annotation.DataIgnore;
import io.github.qishr.cascara.common.property.Properties;

public class VsixThemeInfo extends Contribution {
    String label;
    String uiTheme;
    String path;

    @DataIgnore
    private Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    public String getLabel() {
        // return properties.getString("label");
        return label;
    }

    public VsixThemeInfo setLabel(String s) {
        // properties.set("label", s);
        label = s;
        return this;
    }

    public String getUiTheme() {
        // return properties.getString("uiTheme");
        return uiTheme;
    }

    public VsixThemeInfo setUiTheme(String s) {
        // properties.set("uiTheme", s);
        uiTheme = s;
        return this;
    }

    public String getPath() {
        // String filePath = properties.getString("path");
        // if (filePath.startsWith("./")) {
        //     filePath = filePath.substring(2);
        // }
        // return filePath;
        return path;
    }

    public VsixThemeInfo setPath(String s) {
        // properties.set("path", s);
        path = s;
        return this;
    }

}
