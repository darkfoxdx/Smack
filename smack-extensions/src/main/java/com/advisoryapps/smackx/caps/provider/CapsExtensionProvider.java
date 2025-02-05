/**
 *
 * Copyright © 2009 Jonas Ådahl, 2011-2019 Florian Schmaus
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
package com.advisoryapps.smackx.caps.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.caps.EntityCapsManager;
import com.advisoryapps.smackx.caps.packet.CapsExtension;

public class CapsExtensionProvider extends ExtensionElementProvider<CapsExtension> {

    @Override
    public CapsExtension parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        String hash, version, node;
        if (parser.getEventType() == XmlPullParser.Event.START_ELEMENT
                && parser.getName().equalsIgnoreCase(EntityCapsManager.ELEMENT)) {
            hash = parser.getAttributeValue(null, "hash");
            version = parser.getAttributeValue(null, "ver");
            node = parser.getAttributeValue(null, "node");
        } else {
            // TODO: Should be SmackParsingException.
            throw new IOException("Malformed Caps element");
        }

        parser.next();

        if (!(parser.getEventType() == XmlPullParser.Event.END_ELEMENT
                && parser.getName().equalsIgnoreCase(EntityCapsManager.ELEMENT))) {
            // TODO: Should be SmackParsingException.
            throw new IOException("Malformed nested Caps element");
        }

        if (hash != null && version != null && node != null) {
            return new CapsExtension(node, version, hash);
        } else {
            // TODO: Should be SmackParsingException.
            throw new IOException("Caps element with missing attributes. Attributes: hash=" + hash + " version="
                            + version + " node=" + node);
        }
    }

}
