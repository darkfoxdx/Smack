/**
 *
 * Copyright © 2003-2007 Jive Software, 2014-2019 Florian Schmaus
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
package com.advisoryapps.smack.roster.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.roster.packet.RosterPacket;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;

public class RosterPacketProvider extends IQProvider<RosterPacket> {

    public static final RosterPacketProvider INSTANCE = new RosterPacketProvider();

    @Override
    public RosterPacket parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        RosterPacket roster = new RosterPacket();
        String version = parser.getAttributeValue("", "ver");
        roster.setVersion(version);

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                String startTag = parser.getName();
                switch (startTag) {
                case "item":
                    RosterPacket.Item item = parseItem(parser);
                    roster.addRosterItem(item);
                    break;
                }
                break;
            case END_ELEMENT:
                String endTag = parser.getName();
                switch (endTag) {
                case IQ.QUERY_ELEMENT:
                    if (parser.getDepth() == initialDepth) {
                        break outerloop;
                    }
                }
                break;
            default:
                // Catch all for incomplete switch (MissingCasesInEnumSwitch) statement.
                break;
            }
        }
        return roster;
    }

    public static RosterPacket.Item parseItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        ParserUtils.assertAtStartTag(parser, RosterPacket.Item.ELEMENT);
        final int initialDepth = parser.getDepth();
        String jidString = parser.getAttributeValue("", "jid");
        String itemName = parser.getAttributeValue("", "name");
        BareJid jid = JidCreate.bareFrom(jidString);

        // Create item.
        RosterPacket.Item item = new RosterPacket.Item(jid, itemName);
        // Set status.
        String ask = parser.getAttributeValue("", "ask");
        item.setSubscriptionPending("subscribe".equals(ask));
        // Set type.
        String subscription = parser.getAttributeValue("", "subscription");
        RosterPacket.ItemType type = RosterPacket.ItemType.fromString(subscription);
        item.setItemType(type);
        // Set approval status.
        boolean approved = ParserUtils.getBooleanAttribute(parser, "approved", false);
        item.setApproved(approved);

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                String name = parser.getName();
                switch (name) {
                case RosterPacket.Item.GROUP:
                    final String groupName = parser.nextText();
                    if (groupName != null && groupName.trim().length() > 0) {
                        item.addGroupName(groupName);
                    }
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
        ParserUtils.assertAtEndTag(parser);
        assert item != null;
        return item;
    }
}
