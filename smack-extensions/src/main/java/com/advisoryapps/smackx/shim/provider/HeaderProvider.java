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
package com.advisoryapps.smackx.shim.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.shim.packet.Header;

/**
 * Parses the header element as defined in <a href="http://xmpp.org/extensions/xep-0131">Stanza Headers and Internet Metadata (SHIM)</a>.
 *
 * @author Robin Collier
 */
public class HeaderProvider extends ExtensionElementProvider<Header> {
    @Override
    public Header parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        String name = parser.getAttributeValue(null, "name");
        String value = null;

        parser.next();

        if (parser.getEventType() == XmlPullParser.Event.TEXT_CHARACTERS) {
            value = parser.getText();
        }

        while (parser.getEventType() != XmlPullParser.Event.END_ELEMENT) {
            parser.next();
        }

        return new Header(name, value);
    }

}
