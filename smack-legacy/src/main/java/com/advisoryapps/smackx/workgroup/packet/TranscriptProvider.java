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
import java.util.ArrayList;
import java.util.List;

import com.advisoryapps.smack.packet.Stanza;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

/**
 * An IQProvider for transcripts.
 *
 * @author Gaston Dombiak
 */
public class TranscriptProvider extends IQProvider<Transcript> {

    @Override
    public Transcript parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        String sessionID = parser.getAttributeValue("", "sessionID");
        List<Stanza> packets = new ArrayList<>();

        boolean done = false;
        while (!done) {
            XmlPullParser.Event eventType = parser.next();
            if (eventType == XmlPullParser.Event.START_ELEMENT) {
                if (parser.getName().equals("message")) {
                    packets.add(PacketParserUtils.parseMessage(parser));
                }
                else if (parser.getName().equals("presence")) {
                    packets.add(PacketParserUtils.parsePresence(parser));
                }
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getName().equals("transcript")) {
                    done = true;
                }
            }
        }

        return new Transcript(sessionID, packets);
    }
}
