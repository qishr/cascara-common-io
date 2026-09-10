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

import io.github.qishr.cascara.common.property.Property;
import io.github.qishr.cascara.schema.annotation.SchemaDefinition;
import io.github.qishr.cascara.schema.annotation.SchemaProperty;
import io.github.qishr.cascara.schema.util.SchematicObject;

@SchemaDefinition
public class PackageJsonFile { //extends SchematicObject {

    // @SchemaDefinition
    // public static class Contribution {
    //     @SchemaProperty
    //     List<VsixThemeInfo> themes;
    // }

    // @SchemaDefinition
    // public static class Theme extends Contribution {
    //     // @SchemaProperty
    //     // String name;

    //     // @SchemaProperty
    //     // String uiTheme;

    //     // @SchemaProperty
    //     // String path;
    // }

    // @SchemaDefinition
    // public static class ConfigurationDefaults extends Contribution {}

    @SchemaProperty
    String name;
    // Property<String> name;

    @SchemaProperty
    String displayName;
    // Property<String> displayName;

    @SchemaProperty
    String description;
    // Property<String> description;

    @SchemaProperty
    String version;
    // Property<String> version;

    @SchemaProperty
    String icon;
    // Property<String> icon;

    @SchemaProperty
    String publisher;
    // Property<String> publisher;

    @SchemaProperty
    Map<String,String> engines = new HashMap<>();

    @SchemaProperty
    List<String> categories = new ArrayList<>();

    // TODO: Limit object type to UiTheme, ConfigurationDefaults
    @SchemaProperty
    Map<String,List<Contribution>> contributes = new HashMap<>();

    // private List<Map<String,String>> themes = new ArrayList<>();

    @SchemaProperty
    RepositoryInfo repository;

    public PackageJsonFile() {


        // StringBuilder sb = new StringBuilder();






        // sb.append("  \"repository\": {\n");
        // sb.append("    \"type\": \"git\",\n");
        // sb.append("    \"url\": \"git+https://github.com/sandydunlop/vscode-cascara-retro-theme.git\"\n");
        // sb.append("  }\n");


        // sb.append("}\n");

        // // TODO: Set the defaults:
        // sb.append("  \"version\": \"1.0.2\",\n");
        // sb.append("  \"$schema\": \"vscode://schemas/vscode-extensions\",\n");

        // sb.append("  \"engines\": {\n");
        // sb.append("    \"vscode\": \"^1.103.0\"\n");
        // sb.append("  },\n");
        // sb.append("  \"categories\": [\n");
        // sb.append("    \"Themes\"\n");
        // sb.append("  ],\n");

        // // TODO: Configurables:
        // sb.append("  \"name\": \"cascara-retro-theme\",\n");
        // sb.append("  \"displayName\": \"Cascara Retro Theme\",\n");
        // sb.append("  \"description\": \"Cascara Retro Theme\",\n");
        // sb.append("  \"publisher\": \"Cascara\",\n");
        // sb.append("  \"icon\": \"images/icon.png\",\n");
    }

    public String getName() {
        return name;
    }

    public PackageJsonFile setName(String s) {
        name = s;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PackageJsonFile setDisplayName(String s) {
        displayName = s;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public PackageJsonFile setVersion(String s) {
        version = s;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PackageJsonFile setDescription(String s) {
        description = s;
        return this;
    }

    public String getPublisher() {
        return publisher;
    }

    public PackageJsonFile setPublisher(String s) {
        publisher = s;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public PackageJsonFile setIcon(String s) {
        icon = s;
        return this;
    }

    public Map<String,String> getEngines() {
        return engines;
    }

    public PackageJsonFile setEngines(Map<String,String> m) {
        engines.clear();
        engines.putAll(m);
        return this;
    }

    public PackageJsonFile addEngine(String k, String v) {
        engines.put(k, v);
        return this;
    }

    public List<String> getCategories() {
        return categories;
    }

    public PackageJsonFile setCategories(List<String> c) {
        engines.clear();
        categories.addAll(c);
        return this;
    }

    public PackageJsonFile addCategory(String s) {
        categories.add(s);
        return this;
    }

    public Map<String,List<Contribution>> getContributions() {
        return contributes;
    }

    // public PackageJsonFile addContribution(String type, Contribution c) {
    // }

    public PackageJsonFile addTheme(VsixThemeInfo c) {
        final String groupName = "themes";
        List<Contribution> group = contributes.get(groupName);
        if (group == null) {
            group = new ArrayList<>();
            contributes.put(groupName, group);
        }

        group.add(c);

        return this;
    }

    public RepositoryInfo getRepository() {
        if (repository == null) {
            repository = new RepositoryInfo();
        }
        return repository;
    }

    public PackageJsonFile setRepository(RepositoryInfo o) {
        repository = o;
        return this;
    }

}
