/**
 *
 * Copyright 2013-2014 Georg Lukas
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
package com.advisoryapps.smackx.carbons.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.carbons.packet.CarbonExtension;
import com.advisoryapps.smackx.carbons.packet.CarbonExtension.Direction;
import com.advisoryapps.smackx.forward.packet.Forwarded;
import com.advisoryapps.smackx.forward.provider.ForwardedProvider;

/**
 * This class implements the {@link ExtensionElementProvider} to parse
 * carbon copied messages from a packet.  It will return a {@link CarbonExtension} stanza extension.
 *
 * @author Georg Lukas
 *
 */
public class CarbonManagerProvider extends ExtensionElementProvider<CarbonExtension> {

    private static final ForwardedProvider FORWARDED_PROVIDER = new ForwardedProvider();

    @Override
    public CarbonExtension parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        Direction dir = Direction.valueOf(parser.getName());
        Forwarded fwd = null;

        boolean done = false;
        while (!done) {
            XmlPullParser.Event eventType = parser.next();
            if (eventType == XmlPullParser.Event.START_ELEMENT && parser.getName().equals("forwarded")) {
                fwd = FORWARDED_PROVIDER.parse(parser);
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT && dir == Direction.valueOf(parser.getName()))
                done = true;
        }
        if (fwd == null) {
            // TODO: Should be SmackParseException.
            throw new IOException("sent/received must contain exactly one <forwarded> tag");
        }
        return new CarbonExtension(dir, fwd);
    }
}
