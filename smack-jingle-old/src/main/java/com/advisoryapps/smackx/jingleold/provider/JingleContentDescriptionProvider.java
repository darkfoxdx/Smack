/**
 *
 * Copyright 2003-2005 Jive Software.
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
package com.advisoryapps.smackx.jingleold.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.jingleold.media.PayloadType;
import com.advisoryapps.smackx.jingleold.packet.JingleContentDescription;
import com.advisoryapps.smackx.jingleold.packet.JingleContentDescription.JinglePayloadType;

/**
 * Parser for a Jingle description.
 *
 * @author Alvaro Saurin
 */
public abstract class JingleContentDescriptionProvider extends ExtensionElementProvider<JingleContentDescription> {

    /**
     * Parse a iq/jingle/description/payload-type element.
     *
     * @param parser the input to parse
     * @return a payload type element
     */
    protected JinglePayloadType parsePayload(final XmlPullParser parser) {
        int ptId = 0;
        String ptName;
        int ptChannels = 0;

        try {
            ptId = Integer.parseInt(parser.getAttributeValue("", "id"));
        } catch (Exception e) {
        }

        ptName = parser.getAttributeValue("", "name");

        try {
            ptChannels = Integer.parseInt(parser.getAttributeValue("", "channels"));
        } catch (Exception e) {
        }

        return new JinglePayloadType(new PayloadType(ptId, ptName, ptChannels));
    }

    /**
     * Parse a iq/jingle/description element.
     *
     * @param parser the input to parse
     * @return a description element
     * @throws IOException if an I/O error occured.
     * @throws XmlPullParserException if an error in the XML parser occured.
     */
    @Override
    public JingleContentDescription parse(XmlPullParser parser,
                    int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException,
                    IOException {
        boolean done = false;
        JingleContentDescription desc = getInstance();

        while (!done) {
            XmlPullParser.Event eventType = parser.next();
            String name = parser.getName();

            if (eventType == XmlPullParser.Event.START_ELEMENT) {
                if (name.equals(JingleContentDescription.JinglePayloadType.NODENAME)) {
                    desc.addJinglePayloadType(parsePayload(parser));
                } else {
                    // TODO: Should be SmackParseException.
                    throw new IOException("Unknow element \"" + name + "\" in content.");
                }
            } else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (name.equals(JingleContentDescription.NODENAME)) {
                    done = true;
                }
            }
        }
        return desc;
    }

    /**
     * Return a new instance of this class. Subclasses must overwrite this
     * method.
     *
     * @return the jingle content description.
     */
    protected abstract JingleContentDescription getInstance();

    /**
     * Jingle audio.
     */
    public static class Audio extends JingleContentDescriptionProvider {

        /**
         * Parse an audio payload type.
         */
        @Override
        public JinglePayloadType parsePayload(final XmlPullParser parser) {
            JinglePayloadType pte = super.parsePayload(parser);
            PayloadType.Audio pt = new PayloadType.Audio(pte.getPayloadType());
            int ptClockRate = 0;

            try {
                ptClockRate = Integer.parseInt(parser.getAttributeValue("", "clockrate"));
            } catch (Exception e) {
            }
            pt.setClockRate(ptClockRate);

            return new JinglePayloadType.Audio(pt);
        }

        /**
         * Get a new instance of this object.
         */
        @Override
        protected JingleContentDescription getInstance() {
            return new JingleContentDescription.Audio();
        }
    }
}
