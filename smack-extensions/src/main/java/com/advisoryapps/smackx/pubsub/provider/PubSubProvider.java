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
package com.advisoryapps.smackx.pubsub.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.pubsub.packet.PubSub;
import com.advisoryapps.smackx.pubsub.packet.PubSubNamespace;

/**
 * Parses the root PubSub stanza extensions of the {@link IQ} stanza and returns
 * a {@link PubSub} instance.
 *
 * @author Robin Collier
 */
public class PubSubProvider extends IQProvider<PubSub> {
    @Override
    public PubSub parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        String namespace = parser.getNamespace();
        PubSubNamespace pubSubNamespace = PubSubNamespace.valueOfFromXmlns(namespace);
        PubSub pubsub = new PubSub(pubSubNamespace);

        outerloop: while (true)  {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                PacketParserUtils.addExtensionElement(pubsub, parser, xmlEnvironment);
                break;
            case END_ELEMENT:
                if (parser.getDepth() == initialDepth) {
                    break outerloop;
                }
                break;
            default:
                // Catch all for incomplete switch (MissingCasesInEnumSwitch) statement.
                break;
            }
        }
        return pubsub;
    }
}
