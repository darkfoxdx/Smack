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

package com.advisoryapps.smackx.muc.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.muc.packet.MUCUser;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityJid;

/**
 * The MUCUserProvider parses packets with extended presence information about
 * roles and affiliations.
 *
 * @author Gaston Dombiak
 */
public class MUCUserProvider extends ExtensionElementProvider<MUCUser> {

    /**
     * Parses a MUCUser stanza (extension sub-packet).
     *
     * @param parser the XML parser, positioned at the starting element of the extension.
     * @return a PacketExtension.
     * @throws IOException if an I/O error occured.
     * @throws XmlPullParserException if an error in the XML parser occured.
     */
    @Override
    public MUCUser parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        MUCUser mucUser = new MUCUser();
        outerloop: while (true) {
            switch (parser.next()) {
            case START_ELEMENT:
                switch (parser.getName()) {
                case "invite":
                    mucUser.setInvite(parseInvite(parser));
                    break;
                case "item":
                    mucUser.setItem(MUCParserUtils.parseItem(parser));
                    break;
                case "password":
                    mucUser.setPassword(parser.nextText());
                    break;
                case "status":
                    String statusString = parser.getAttributeValue("", "code");
                    mucUser.addStatusCode(MUCUser.Status.create(statusString));
                    break;
                case "decline":
                    mucUser.setDecline(parseDecline(parser));
                    break;
                case "destroy":
                    mucUser.setDestroy(MUCParserUtils.parseDestroy(parser));
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

        return mucUser;
    }

    private static MUCUser.Invite parseInvite(XmlPullParser parser) throws XmlPullParserException, IOException {
        String reason = null;
        EntityBareJid to = ParserUtils.getBareJidAttribute(parser, "to");
        EntityJid from = ParserUtils.getEntityJidAttribute(parser, "from");

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            if (eventType == XmlPullParser.Event.START_ELEMENT) {
                if (parser.getName().equals("reason")) {
                    reason = parser.nextText();
                }
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getName().equals("invite")) {
                    break outerloop;
                }
            }
        }
        return new MUCUser.Invite(reason, from, to);
    }

    private static MUCUser.Decline parseDecline(XmlPullParser parser) throws XmlPullParserException, IOException {
        String reason = null;
        EntityBareJid to = ParserUtils.getBareJidAttribute(parser, "to");
        EntityBareJid from = ParserUtils.getBareJidAttribute(parser, "from");

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            if (eventType == XmlPullParser.Event.START_ELEMENT) {
                if (parser.getName().equals("reason")) {
                    reason = parser.nextText();
                }
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getName().equals("decline")) {
                    break outerloop;
                }
            }
        }
        return new MUCUser.Decline(reason, from, to);
    }
}
