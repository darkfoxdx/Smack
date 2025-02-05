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

package com.advisoryapps.smackx.workgroup.packet;

import java.io.IOException;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import org.jxmpp.jid.Jid;

/**
 * An IQProvider class which has savvy about the offer-revoke tag.<br>
 *
 * @author loki der quaeler
 */
public class OfferRevokeProvider extends IQProvider<IQ> {

    @Override
    public OfferRevokePacket parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        // The parser will be positioned on the opening IQ tag, so get the JID attribute.
        Jid userJID = ParserUtils.getJidAttribute(parser);
        // Default the userID to the JID.
        Jid userID = userJID;
        String reason = null;
        String sessionID = null;
        boolean done = false;

        while (!done) {
            XmlPullParser.Event eventType = parser.next();

            if ((eventType == XmlPullParser.Event.START_ELEMENT) && parser.getName().equals("reason")) {
                reason = parser.nextText();
            }
            else if ((eventType == XmlPullParser.Event.START_ELEMENT)
                         && parser.getName().equals(SessionID.ELEMENT_NAME)) {
                sessionID = parser.getAttributeValue("", "id");
            }
            else if ((eventType == XmlPullParser.Event.START_ELEMENT)
                         && parser.getName().equals(UserID.ELEMENT_NAME)) {
                userID = ParserUtils.getJidAttribute(parser, "id");
            }
            else if ((eventType == XmlPullParser.Event.END_ELEMENT) && parser.getName().equals(
                    "offer-revoke")) {
                done = true;
            }
        }

        return new OfferRevokePacket(userJID, userID, reason, sessionID);
    }

    public static class OfferRevokePacket extends IQ {

        public static final String ELEMENT = "offer-revoke";
        public static final String NAMESPACE = "http://jabber.org/protocol/workgroup";
        private final Jid userJID;
        private final Jid userID;
        private final String sessionID;
        private final String reason;

        public OfferRevokePacket (Jid userJID, Jid userID, String cause, String sessionID) {
            super(ELEMENT, NAMESPACE);
            this.userJID = userJID;
            this.userID = userID;
            this.reason = cause;
            this.sessionID = sessionID;
        }

        public Jid getUserJID() {
            return userJID;
        }

        public Jid getUserID() {
            return this.userID;
        }

        public String getReason() {
            return this.reason;
        }

        public String getSessionID() {
            return this.sessionID;
        }

        @Override
        protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
            buf.append(" jid=\"").append(userID).append("\">");
            if (reason != null) {
                buf.append("<reason>").append(reason).append("</reason>");
            }
            if (sessionID != null) {
                buf.append(new SessionID(sessionID).toXML());
            }
            if (userID != null) {
                buf.append(new UserID(userID).toXML());
            }
            return buf;
        }
    }
}
