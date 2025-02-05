/**
 *
 * Copyright 2013-2014 Georg Lukas
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
package com.advisoryapps.smackx.forward.provider;

import java.io.IOException;
import java.util.logging.Logger;

import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.packet.Stanza;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.delay.packet.DelayInformation;
import com.advisoryapps.smackx.delay.provider.DelayInformationProvider;
import com.advisoryapps.smackx.forward.packet.Forwarded;

/**
 * This class implements the {@link ExtensionElementProvider} to parse
 * forwarded messages from a packet.  It will return a {@link Forwarded} stanza extension.
 *
 * @author Georg Lukas
 */
public class ForwardedProvider extends ExtensionElementProvider<Forwarded> {

    public static final ForwardedProvider INSTANCE = new ForwardedProvider();

    private static final Logger LOGGER = Logger.getLogger(ForwardedProvider.class.getName());

    @Override
    public Forwarded parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        DelayInformation di = null;
        Stanza packet = null;

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                String name = parser.getName();
                String namespace = parser.getNamespace();
                switch (name) {
                case DelayInformation.ELEMENT:
                    if (DelayInformation.NAMESPACE.equals(namespace)) {
                        di = DelayInformationProvider.INSTANCE.parse(parser, parser.getDepth(), null);
                    } else {
                        LOGGER.warning("Namespace '" + namespace + "' does not match expected namespace '"
                                        + DelayInformation.NAMESPACE + "'");
                    }
                    break;
                case Message.ELEMENT:
                    packet = PacketParserUtils.parseMessage(parser);
                    break;
                default:
                    LOGGER.warning("Unsupported forwarded packet type: " + name);
                }
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

        if (packet == null) {
            // TODO: Should be SmackParseException.
            throw new IOException("forwarded extension must contain a packet");
        }
        return new Forwarded(di, packet);
    }
}
