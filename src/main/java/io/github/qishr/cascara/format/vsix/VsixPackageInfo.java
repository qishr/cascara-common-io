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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.github.qishr.cascara.common.util.Properties;
import io.github.qishr.cascara.format.vsix.VsixPackage;
import io.github.qishr.cascara.format.vsix.VsixThemeInfo;

public class VsixPackageInfo {
    private String name;
    private String displayName;
    private Path path;

    private Properties properties = new Properties();
    private List<String> categories = new ArrayList<>();
    private List<VsixThemeInfo> themes = new ArrayList<>();

    public VsixPackageInfo(Path path, String name, String displayName) {
        this.path = path;
        this.name = name;
        this.displayName = displayName;
    }

    public Properties getProperties() {
        return properties;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<VsixThemeInfo> getThemes() {
        return themes;
    }

    public String getName() {
        return name;
    }
    public String getDisplayName() {
        return displayName;
    }

    public Path getPath() { return path; }
}
