/**
 *
 * Copyright 2017-2019 Florian Schmaus
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
package com.advisoryapps.smackx.jingle.provider;

import java.io.IOException;
import java.util.logging.Logger;

import com.advisoryapps.smack.packet.StandardExtensionElement;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.parsing.StandardExtensionElementProvider;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.jingle.element.Jingle;
import com.advisoryapps.smackx.jingle.element.JingleAction;
import com.advisoryapps.smackx.jingle.element.JingleContent;
import com.advisoryapps.smackx.jingle.element.JingleContentDescription;
import com.advisoryapps.smackx.jingle.element.JingleContentTransport;
import com.advisoryapps.smackx.jingle.element.JingleReason;
import com.advisoryapps.smackx.jingle.element.JingleReason.Reason;
import com.advisoryapps.smackx.jingle.element.UnknownJingleContentDescription;
import com.advisoryapps.smackx.jingle.element.UnknownJingleContentTransport;

import org.jxmpp.jid.FullJid;

public class JingleProvider extends IQProvider<Jingle> {

    private static final Logger LOGGER = Logger.getLogger(JingleProvider.class.getName());

    @Override
    public Jingle parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        Jingle.Builder builder = Jingle.getBuilder();

        String actionString = parser.getAttributeValue("", Jingle.ACTION_ATTRIBUTE_NAME);
        if (actionString != null) {
            JingleAction action = JingleAction.fromString(actionString);
            builder.setAction(action);
        }

        FullJid initiator = ParserUtils.getFullJidAttribute(parser, Jingle.INITIATOR_ATTRIBUTE_NAME);
        builder.setInitiator(initiator);

        FullJid responder = ParserUtils.getFullJidAttribute(parser, Jingle.RESPONDER_ATTRIBUTE_NAME);
        builder.setResponder(responder);

        String sessionId = parser.getAttributeValue("", Jingle.SESSION_ID_ATTRIBUTE_NAME);
        builder.setSessionId(sessionId);


        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                String tagName = parser.getName();
                switch (tagName) {
                case JingleContent.ELEMENT:
                    JingleContent content = parseJingleContent(parser, parser.getDepth());
                    builder.addJingleContent(content);
                    break;
                case JingleReason.ELEMENT:
                    parser.next();
                    String reasonString = parser.getName();
                    JingleReason reason;
                    if (reasonString.equals("alternative-session")) {
                        parser.next();
                        String sid = parser.nextText();
                        reason = new JingleReason.AlternativeSession(sid);
                    } else {
                        reason = new JingleReason(Reason.fromString(reasonString));
                    }
                    builder.setReason(reason);
                    break;
                default:
                    LOGGER.severe("Unknown Jingle element: " + tagName);
                    break;
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

        return builder.build();
    }

    public static JingleContent parseJingleContent(XmlPullParser parser, final int initialDepth)
                    throws XmlPullParserException, IOException, SmackParsingException {
        JingleContent.Builder builder = JingleContent.getBuilder();

        String creatorString = parser.getAttributeValue("", JingleContent.CREATOR_ATTRIBUTE_NAME);
        JingleContent.Creator creator = JingleContent.Creator.valueOf(creatorString);
        builder.setCreator(creator);

        String disposition = parser.getAttributeValue("", JingleContent.DISPOSITION_ATTRIBUTE_NAME);
        builder.setDisposition(disposition);

        String name = parser.getAttributeValue("", JingleContent.NAME_ATTRIBUTE_NAME);
        builder.setName(name);

        String sendersString = parser.getAttributeValue("", JingleContent.SENDERS_ATTRIBUTE_NAME);
        if (sendersString != null) {
            JingleContent.Senders senders = JingleContent.Senders.valueOf(sendersString);
            builder.setSenders(senders);
        }

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                String tagName = parser.getName();
                String namespace = parser.getNamespace();
                switch (tagName) {
                case JingleContentDescription.ELEMENT: {
                    JingleContentDescription description;
                    JingleContentDescriptionProvider<?> provider = JingleContentProviderManager.getJingleContentDescriptionProvider(namespace);
                    if (provider == null) {
                        StandardExtensionElement standardExtensionElement = StandardExtensionElementProvider.INSTANCE.parse(parser);
                        description = new UnknownJingleContentDescription(standardExtensionElement);
                    }
                    else {
                        description = provider.parse(parser);
                    }
                    builder.setDescription(description);
                    break;
                }
                case JingleContentTransport.ELEMENT: {
                    JingleContentTransport transport;
                    JingleContentTransportProvider<?> provider = JingleContentProviderManager.getJingleContentTransportProvider(namespace);
                    if (provider == null) {
                        StandardExtensionElement standardExtensionElement = StandardExtensionElementProvider.INSTANCE.parse(parser);
                        transport = new UnknownJingleContentTransport(standardExtensionElement);
                    }
                    else {
                        transport = provider.parse(parser);
                    }
                    builder.setTransport(transport);
                    break;
                }
                default:
                    LOGGER.severe("Unknown Jingle content element: " + tagName);
                    break;
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

        return builder.build();
    }
}
