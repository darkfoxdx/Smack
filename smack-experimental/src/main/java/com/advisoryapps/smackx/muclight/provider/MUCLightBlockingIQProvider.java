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

import com.advisoryapps.smack.packet.IQ.Type;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.muclight.element.MUCLightBlockingIQ;

import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * MUC Light blocking IQ provider class.
 *
 * @author Fernando Ramirez
 *
 */
public class MUCLightBlockingIQProvider extends IQProvider<MUCLightBlockingIQ> {

    @Override
    public MUCLightBlockingIQ parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        HashMap<Jid, Boolean> rooms = null;
        HashMap<Jid, Boolean> users = null;

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();

            if (eventType == XmlPullParser.Event.START_ELEMENT) {

                if (parser.getName().equals("room")) {
                    rooms = parseBlocking(parser, rooms);
                }

                if (parser.getName().equals("user")) {
                    users = parseBlocking(parser, users);
                }

            } else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getDepth() == initialDepth) {
                    break outerloop;
                }
            }
        }

        MUCLightBlockingIQ mucLightBlockingIQ = new MUCLightBlockingIQ(rooms, users);
        mucLightBlockingIQ.setType(Type.result);
        return mucLightBlockingIQ;
    }

    private static HashMap<Jid, Boolean> parseBlocking(XmlPullParser parser, HashMap<Jid, Boolean> map)
            throws XmppStringprepException, XmlPullParserException, IOException {
        if (map == null) {
            map = new HashMap<>();
        }
        String action = parser.getAttributeValue("", "action");

        if (action.equals("deny")) {
            map.put(JidCreate.from(parser.nextText()), false);
        } else if (action.equals("allow")) {
            map.put(JidCreate.from(parser.nextText()), true);
        }
        return map;
    }

}
