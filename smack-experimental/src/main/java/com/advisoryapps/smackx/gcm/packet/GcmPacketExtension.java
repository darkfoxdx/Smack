/**
 *
 * Copyright © 2014 Florian Schmaus
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
package com.advisoryapps.smackx.gcm.packet;

import com.advisoryapps.smack.packet.Stanza;

import com.advisoryapps.smackx.json.packet.AbstractJsonPacketExtension;

/**
 * XMPP extension elements as used by Google's GCM Cloud Connection Server (XMPP).
 * <p>
 * This extension is semantically the same as {@link com.advisoryapps.smackx.json.packet.JsonPacketExtension}, but with
 * a different element and namespace. It is used to exchange message stanzas with a JSON payload as extension element.
 * </p>
 *
 * @see <a href="https://developer.android.com/google/gcm/ccs.html">GCM Cloud Connection Server (XMPP)</a>
 */
public class GcmPacketExtension extends AbstractJsonPacketExtension {

    public static final String ELEMENT = "gcm";
    public static final String NAMESPACE = "google:mobile:data";

    public GcmPacketExtension(String json) {
        super(json);
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    /**
     * Retrieve the GCM stanza extension from the packet.
     *
     * @param packet TODO javadoc me please
     * @return the GCM stanza extension or null.
     */
    public static GcmPacketExtension from(Stanza packet) {
        return packet.getExtension(ELEMENT, NAMESPACE);
    }
}
