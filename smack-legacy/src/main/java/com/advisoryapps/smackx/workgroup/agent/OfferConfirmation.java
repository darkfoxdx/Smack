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

package com.advisoryapps.smackx.workgroup.agent;

import java.io.IOException;

import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.SimpleIQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import org.jxmpp.jid.Jid;


public class OfferConfirmation extends SimpleIQ {
    private String userJID;
    private long sessionID;

    public OfferConfirmation() {
        super("offer-confirmation", "http://jabber.org/protocol/workgroup");
    }

    public String getUserJID() {
        return userJID;
    }

    public void setUserJID(String userJID) {
        this.userJID = userJID;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }


    public void notifyService(XMPPConnection con, Jid workgroup, String createdRoomName) throws NotConnectedException, InterruptedException {
        NotifyServicePacket packet = new NotifyServicePacket(workgroup, createdRoomName);
        con.sendStanza(packet);
    }

    public static class Provider extends IQProvider<OfferConfirmation> {

        @Override
        public OfferConfirmation parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment)
                        throws XmlPullParserException, IOException {
            final OfferConfirmation confirmation = new OfferConfirmation();

            boolean done = false;
            while (!done) {
                parser.next();
                String elementName = parser.getName();
                if (parser.getEventType() == XmlPullParser.Event.START_ELEMENT && "user-jid".equals(elementName)) {
                    try {
                        confirmation.setUserJID(parser.nextText());
                    }
                    catch (NumberFormatException nfe) {
                    }
                }
                else if (parser.getEventType() == XmlPullParser.Event.START_ELEMENT && "session-id".equals(elementName)) {
                    try {
                        confirmation.setSessionID(Long.valueOf(parser.nextText()));
                    }
                    catch (NumberFormatException nfe) {
                    }
                }
                else if (parser.getEventType() == XmlPullParser.Event.END_ELEMENT && "offer-confirmation".equals(elementName)) {
                    done = true;
                }
            }


            return confirmation;
        }
    }


    /**
     * Stanza for notifying server of RoomName
     */
    private static class NotifyServicePacket extends IQ {
        String roomName;

        NotifyServicePacket(Jid workgroup, String roomName) {
            super("offer-confirmation", "http://jabber.org/protocol/workgroup");
            this.setTo(workgroup);
            this.setType(IQ.Type.result);

            this.roomName = roomName;
        }

        @Override
        protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
            xml.attribute("roomname", roomName);
            xml.setEmptyElement();
            return xml;
        }
    }


}
