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

package com.advisoryapps.smackx.disco.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.disco.packet.DiscoverInfo;

/**
* The DiscoverInfoProvider parses Service Discovery information packets.
*
* @author Gaston Dombiak
*/
public class DiscoverInfoProvider extends IQProvider<DiscoverInfo> {

    @Override
    public DiscoverInfo parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        DiscoverInfo discoverInfo = new DiscoverInfo();
        boolean done = false;
        DiscoverInfo.Identity identity;
        String category = "";
        String identityName = "";
        String type = "";
        String variable = "";
        String lang = "";
        discoverInfo.setNode(parser.getAttributeValue("", "node"));
        while (!done) {
            XmlPullParser.Event eventType = parser.next();
            if (eventType == XmlPullParser.Event.START_ELEMENT) {
                final String name = parser.getName();
                final String namespace = parser.getNamespace();
                if (namespace.equals(DiscoverInfo.NAMESPACE)) {
                    switch (name) {
                    case "identity":
                        // Initialize the variables from the parsed XML
                        category = parser.getAttributeValue("", "category");
                        identityName = parser.getAttributeValue("", "name");
                        type = parser.getAttributeValue("", "type");
                        lang = parser.getAttributeValue(parser.getNamespace("xml"), "lang");
                        break;
                    case "feature":
                        // Initialize the variables from the parsed XML
                        variable = parser.getAttributeValue("", "var");
                        break;
                    }
                }
                // Otherwise, it must be a packet extension.
                else {
                    PacketParserUtils.addExtensionElement(discoverInfo, parser, xmlEnvironment);
                }
            } else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getName().equals("identity")) {
                    // Create a new identity and add it to the discovered info.
                    identity = new DiscoverInfo.Identity(category, type, identityName, lang);
                    discoverInfo.addIdentity(identity);
                }
                if (parser.getName().equals("feature")) {
                    // Create a new feature and add it to the discovered info.
                    boolean notADuplicateFeature = discoverInfo.addFeature(variable);
                    assert notADuplicateFeature;
                }
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
        }

        return discoverInfo;
    }
}
