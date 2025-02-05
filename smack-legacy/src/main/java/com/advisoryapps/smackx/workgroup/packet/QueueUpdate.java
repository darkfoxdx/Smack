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

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

/**
 * An IQ stanza that encapsulates both types of workgroup queue
 * status notifications -- position updates, and estimated time
 * left in the queue updates.
 */
public class QueueUpdate implements ExtensionElement {

    /**
     * Element name of the stanza extension.
     */
    public static final String ELEMENT_NAME = "queue-status";

    /**
     * Namespace of the stanza extension.
     */
    public static final String NAMESPACE = "http://jabber.org/protocol/workgroup";

    private final int position;
    private final int remainingTime;

    public QueueUpdate(int position, int remainingTime) {
        this.position = position;
        this.remainingTime = remainingTime;
    }

    /**
     * Returns the user's position in the workgroup queue, or -1 if the
     * value isn't set on this packet.
     *
     * @return the position in the workgroup queue.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Returns the user's estimated time left in the workgroup queue, or
     * -1 if the value isn't set on this packet.
     *
     * @return the estimated time left in the workgroup queue.
     */
    public int getRemaingTime() {
        return remainingTime;
    }

    @Override
    public String toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
        StringBuilder buf = new StringBuilder();
        buf.append("<queue-status xmlns=\"http://jabber.org/protocol/workgroup\">");
        if (position != -1) {
            buf.append("<position>").append(position).append("</position>");
        }
        if (remainingTime != -1) {
            buf.append("<time>").append(remainingTime).append("</time>");
        }
        buf.append("</queue-status>");
        return buf.toString();
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    public static class Provider extends ExtensionElementProvider<QueueUpdate> {

        @Override
        public QueueUpdate parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment)
                        throws XmlPullParserException, IOException {
            boolean done = false;
            int position = -1;
            int timeRemaining = -1;
            while (!done) {
                parser.next();
                String elementName = parser.getName();
                if (parser.getEventType() == XmlPullParser.Event.START_ELEMENT && "position".equals(elementName)) {
                    try {
                        position = Integer.parseInt(parser.nextText());
                    }
                    catch (NumberFormatException nfe) {
                    }
                }
                else if (parser.getEventType() == XmlPullParser.Event.START_ELEMENT && "time".equals(elementName)) {
                    try {
                        timeRemaining = Integer.parseInt(parser.nextText());
                    }
                    catch (NumberFormatException nfe) {
                    }
                }
                else if (parser.getEventType() == XmlPullParser.Event.END_ELEMENT && "queue-status".equals(elementName)) {
                    done = true;
                }
            }
            return new QueueUpdate(position, timeRemaining);
        }
    }
}
