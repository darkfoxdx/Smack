/**
 *
 * Copyright the original author or authors
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
package com.advisoryapps.smackx.pubsub;

import com.advisoryapps.smack.packet.ExtensionElement;

import com.advisoryapps.smackx.pubsub.packet.PubSubNamespace;

/**
 * Represents and item that has been deleted from a node.
 *
 * @author Robin Collier
 */
public class RetractItem implements ExtensionElement {
    private final String id;

    /**
     * Construct a <code>RetractItem</code> with the specified id.
     *
     * @param itemId The id if the item deleted
     */
    public RetractItem(String itemId) {
        if (itemId == null)
            throw new IllegalArgumentException("itemId must not be 'null'");
        id = itemId;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getElementName() {
        return "retract";
    }

    @Override
    public String getNamespace() {
        return PubSubNamespace.event.getXmlns();
    }

    @Override
    public String toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
        return "<retract id='" + id + "'/>";
    }
}
