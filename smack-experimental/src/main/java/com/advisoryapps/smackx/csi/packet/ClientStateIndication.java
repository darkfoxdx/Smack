/**
 *
 * Copyright © 2014-2015 Florian Schmaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.advisoryapps.smackx.csi.packet;

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.packet.Nonza;

/**
 * Client State Indication.
 * @see <a href="http://xmpp.org/extensions/xep-0352.html">XEP-0352: Client State Indication</a>
 *
 */
public class ClientStateIndication {
    public static final String NAMESPACE = "urn:xmpp:csi:0";

    public static final class Active implements Nonza {
        public static final Active INSTANCE = new Active();
        public static final String ELEMENT = "active";

        private Active() {
        }

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }

        @Override
        public String getElementName() {
            return ELEMENT;
        }

        @Override
        public String toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
            return '<' + ELEMENT + " xmlns='" + NAMESPACE + "'/>";
        }
    }

    public static final class Inactive implements Nonza {
        public static final Inactive INSTANCE = new Inactive();
        public static final String ELEMENT = "inactive";

        private Inactive() {
        }

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }

        @Override
        public String getElementName() {
            return ELEMENT;
        }

        @Override
        public String toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
            return '<' + ELEMENT + " xmlns='" + NAMESPACE + "'/>";
        }
    }

    public static final class Feature implements ExtensionElement {
        public static final Feature INSTANCE = new Feature();
        public static final String ELEMENT = "csi";

        private Feature() {
        }

        @Override
        public String getElementName() {
            return ELEMENT;
        }

        @Override
        public String toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
            return '<' + ELEMENT + " xmlns='" + NAMESPACE + "'/>";
        }

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }
    }
}
