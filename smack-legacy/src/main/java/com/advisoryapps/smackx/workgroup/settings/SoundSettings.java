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

package com.advisoryapps.smackx.workgroup.settings;

import java.io.IOException;

import com.advisoryapps.smack.packet.SimpleIQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.stringencoder.Base64;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

public class SoundSettings extends SimpleIQ {
    private String outgoingSound;
    private String incomingSound;


    public void setOutgoingSound(String outgoingSound) {
        this.outgoingSound = outgoingSound;
    }

    public void setIncomingSound(String incomingSound) {
        this.incomingSound = incomingSound;
    }

    public byte[] getIncomingSoundBytes() {
        return Base64.decode(incomingSound);
    }

    public byte[] getOutgoingSoundBytes() {
        return Base64.decode(outgoingSound);
    }


    /**
     * Element name of the stanza extension.
     */
    public static final String ELEMENT_NAME = "sound-settings";

    /**
     * Namespace of the stanza extension.
     */
    public static final String NAMESPACE = "http://jivesoftware.com/protocol/workgroup";

    public SoundSettings() {
        super(ELEMENT_NAME, NAMESPACE);
    }

    /**
     * Stanza extension provider for SoundSetting Packets.
     */
    public static class InternalProvider extends IQProvider<SoundSettings> {

        @Override
        public SoundSettings parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
            SoundSettings soundSettings = new SoundSettings();

            boolean done = false;


            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT && "outgoingSound".equals(parser.getName())) {
                    soundSettings.setOutgoingSound(parser.nextText());
                }
                else if ((eventType == XmlPullParser.Event.START_ELEMENT) && "incomingSound".equals(parser.getName())) {
                    soundSettings.setIncomingSound(parser.nextText());
                }
                else if (eventType == XmlPullParser.Event.END_ELEMENT && "sound-settings".equals(parser.getName())) {
                    done = true;
                }
            }

            return soundSettings;
        }
    }
}

