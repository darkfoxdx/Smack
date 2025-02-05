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

import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.muc.MUCAffiliation;
import com.advisoryapps.smackx.muc.MUCRole;
import com.advisoryapps.smackx.muc.packet.Destroy;
import com.advisoryapps.smackx.muc.packet.MUCItem;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

public class MUCParserUtils {
    public static MUCItem parseItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        int initialDepth = parser.getDepth();
        MUCAffiliation affiliation = MUCAffiliation.fromString(parser.getAttributeValue("", "affiliation"));
        Resourcepart nick = ParserUtils.getResourcepartAttribute(parser, "nick");
        MUCRole role = MUCRole.fromString(parser.getAttributeValue("", "role"));
        Jid jid = ParserUtils.getJidAttribute(parser);
        Jid actor = null;
        Resourcepart actorNick = null;
        String reason = null;
        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                String name = parser.getName();
                switch (name) {
                case "actor":
                    actor = ParserUtils.getJidAttribute(parser);
                    // TODO change to
                    // actorNick = Resourcepart.from(parser.getAttributeValue("", "nick"));
                    // once a newer version of JXMPP is used that supports from(null).
                    String actorNickString = parser.getAttributeValue("", "nick");
                    if (actorNickString != null) {
                        actorNick = Resourcepart.from(actorNickString);
                    }
                    break;
                case "reason":
                    reason = parser.nextText();
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
        return new MUCItem(affiliation, role, actor, reason, jid, nick, actorNick);
    }

    public static Destroy parseDestroy(XmlPullParser parser) throws XmlPullParserException, IOException {
        final int initialDepth = parser.getDepth();
        final EntityBareJid jid = ParserUtils.getBareJidAttribute(parser);
        String reason = null;
        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                final String name = parser.getName();
                switch (name) {
                case "reason":
                    reason = parser.nextText();
                    break;
                }
                break;
            case END_ELEMENT:
                if (initialDepth == parser.getDepth()) {
                    break outerloop;
                }
                break;
            default:
                // Catch all for incomplete switch (MissingCasesInEnumSwitch) statement.
                break;
            }
        }
        return new Destroy(jid, reason);
    }
}
