/**
 *
 * Copyright 2003-2007 Jive Software.
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

package com.advisoryapps.smack.filter;

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.packet.Stanza;
import com.advisoryapps.smack.util.StringUtils;

/**
 * Filters for packets with a particular type of stanza extension.
 *
 * @author Matt Tucker
 * @deprecated use {@link StanzaExtensionFilter} instead.
 */
@Deprecated
public class PacketExtensionFilter implements StanzaFilter {

    private final String elementName;
    private final String namespace;

    /**
     * Creates a new stanza extension filter. Packets will pass the filter if
     * they have a stanza extension that matches the specified element name
     * and namespace.
     *
     * @param elementName the XML element name of the stanza extension.
     * @param namespace the XML namespace of the stanza extension.
     */
    public PacketExtensionFilter(String elementName, String namespace) {
        StringUtils.requireNotNullNorEmpty(namespace, "namespace must not be null nor empty");

        this.elementName = elementName;
        this.namespace = namespace;
    }

    /**
     * Creates a new stanza extension filter. Packets will pass the filter if they have a packet
     * extension that matches the specified namespace.
     *
     * @param namespace the XML namespace of the stanza extension.
     */
    public PacketExtensionFilter(String namespace) {
        this(null, namespace);
    }

    /**
     * Creates a new stanza extension filter for the given stanza extension.
     *
     * @param packetExtension TODO javadoc me please
     */
    public PacketExtensionFilter(ExtensionElement packetExtension) {
        this(packetExtension.getElementName(), packetExtension.getNamespace());
    }

    @Override
    public boolean accept(Stanza packet) {
        return packet.hasExtension(elementName, namespace);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": element=" + elementName + " namespace=" + namespace;
    }
}
