/**
 *
 * Copyright 2016 Fernando Ramirez
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
package com.advisoryapps.smackx.muclight.provider;

import java.io.IOException;
import java.util.HashMap;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.muclight.MUCLightRoomConfiguration;
import com.advisoryapps.smackx.muclight.element.MUCLightConfigurationIQ;

/**
 * MUC Light configuration IQ provider class.
 *
 * @author Fernando Ramirez
 *
 */
public class MUCLightConfigurationIQProvider extends IQProvider<MUCLightConfigurationIQ> {

    @Override
    public MUCLightConfigurationIQ parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        String version = null;
        String roomName = null;
        String subject = null;
        HashMap<String, String> customConfigs = null;

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();

            if (eventType == XmlPullParser.Event.START_ELEMENT) {

                if (parser.getName().equals("version")) {
                    version = parser.nextText();
                } else if (parser.getName().equals("roomname")) {
                    roomName = parser.nextText();
                } else if (parser.getName().equals("subject")) {
                    subject = parser.nextText();
                } else {
                    if (customConfigs == null) {
                        customConfigs = new HashMap<>();
                    }
                    customConfigs.put(parser.getName(), parser.nextText());
                }

            } else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getDepth() == initialDepth) {
                    break outerloop;
                }
            }
        }

        MUCLightRoomConfiguration configuration = new MUCLightRoomConfiguration(roomName, subject, customConfigs);

        return new MUCLightConfigurationIQ(version, configuration);
    }

}
