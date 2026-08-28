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


module cascara.common.io {
    requires java.net.http;
    requires transitive cascara.common;
    requires cascara.lang.yaml;

    exports io.github.qishr.cascara.common.content;
    exports io.github.qishr.cascara.common.content.type;
    exports io.github.qishr.cascara.common.io;
    exports io.github.qishr.cascara.common.io.filewatcher;
    exports io.github.qishr.cascara.common.io.provider;

    opens io.github.qishr.cascara.common.content to cascara.common;
    opens io.github.qishr.cascara.common.content.type to cascara.common;

    uses io.github.qishr.cascara.common.service.ServiceProvider;

    provides io.github.qishr.cascara.common.io.provider.ResourceProvider
        with io.github.qishr.cascara.common.io.provider.CascaraResourceProvider,
             io.github.qishr.cascara.common.io.provider.FileResourceProvider,
             io.github.qishr.cascara.common.io.provider.HttpResourceProvider,
             io.github.qishr.cascara.common.io.provider.ResResourceProvider;
}
