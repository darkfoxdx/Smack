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
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

public class MonitorPacket extends IQ {

    private String sessionID;

    private boolean isMonitor;

    public boolean isMonitor() {
        return isMonitor;
    }

    public void setMonitor(boolean monitor) {
        isMonitor = monitor;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    /**
     * Element name of the stanza extension.
     */
    public static final String ELEMENT_NAME = "monitor";

    /**
     * Namespace of the stanza extension.
     */
    public static final String NAMESPACE = "http://jivesoftware.com/protocol/workgroup";

    public MonitorPacket() {
        super(ELEMENT_NAME, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        buf.rightAngleBracket();

        if (sessionID != null) {
            buf.append("<makeOwner sessionID=\"" + sessionID + "\"></makeOwner>");
        }

        return buf;
    }


    /**
     * Stanza extension provider for Monitor Packets.
     */
    public static class InternalProvider extends IQProvider<MonitorPacket> {

        @Override
        public MonitorPacket parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
            MonitorPacket packet = new MonitorPacket();

            boolean done = false;


            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT && "isMonitor".equals(parser.getName())) {
                    String value = parser.nextText();
                    if ("false".equalsIgnoreCase(value)) {
                        packet.setMonitor(false);
                    }
                    else {
                        packet.setMonitor(true);
                    }
                }
                else if (eventType == XmlPullParser.Event.END_ELEMENT && "monitor".equals(parser.getName())) {
                    done = true;
                }
            }

            return packet;
        }
    }
}
