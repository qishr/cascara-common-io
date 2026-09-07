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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VsixMetadata {
    // TODO: This should extend ObservableObject

    private String name;
    private String displayName;
    private String version;
    private String description;
    private String publisher;
    private String icon;
    private Map<String,String> engines = new HashMap<>();
    private RepositoryInfo repository;

    // private Properties properties = new Properties();
    private List<String> categories = new ArrayList<>();
    private List<VsixThemeInfo> themes = new ArrayList<>();

    public VsixMetadata() {
    }

    public void set(String k, String v) {
        switch(k) {
            case "name" -> setName(v);
            case "displayName" -> setDisplayName(v);
            case "description" -> setDescription(v);
            case "version" -> setVersion(v);
            case "publisher" -> setPublisher(v);
        }
    }

    public String getName() {
        return name;
    }

    public VsixMetadata setName(String s) {
        name = s;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public VsixMetadata setDisplayName(String s) {
        displayName = s;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public VsixMetadata setVersion(String s) {
        version = s;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VsixMetadata setDescription(String s) {
        description = s;
        return this;
    }

    public String getPublisher() {
        return publisher;
    }

    public VsixMetadata setPublisher(String s) {
        publisher = s;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public VsixMetadata setIcon(String s) {
        icon = s;
        return this;
    }

    public Map<String,String> getEngines() {
        return engines;
    }

    public VsixMetadata setEngines(Map<String,String> m) {
        engines.clear();
        engines.putAll(m);
        return this;
    }

    public VsixMetadata addEngine(String k, String v) {
        engines.put(k, v);
        return this;
    }

    public RepositoryInfo getRepository() {
        if (repository == null) {
            repository = new RepositoryInfo();
        }
        return repository;
    }

    public VsixMetadata setRepository(RepositoryInfo o) {
        repository = o;
        return this;
    }

    //
    //
    //

    // public Properties getProperties() {
    //     return properties;
    // }

    public List<String> getCategories() {
        return categories;
    }

    public VsixMetadata setCategories(List<String> l) {
        categories.retainAll(l);
        categories.addAll(l);
        return this;
    }

    public List<VsixThemeInfo> getThemes() {
        return themes;
    }

    // public VsixMetadata setThemes(List<String> l) {
    //     themes.retainAll(l);
    //     themes.addAll(l);
    //     return this;
    // }

}
