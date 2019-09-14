/**
 *
 * Copyright © 2014-2019 Florian Schmaus
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
package com.advisoryapps.smackx.delay.provider;

import java.io.IOException;
import java.util.Date;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException.SmackTextParseException;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.delay.packet.DelayInformation;

public abstract class AbstractDelayInformationProvider extends ExtensionElementProvider<DelayInformation> {

    @Override
    public final DelayInformation parse(XmlPullParser parser,
                    int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException,
                    IOException, SmackTextParseException {
        String stampString = parser.getAttributeValue("", "stamp");
        String from = parser.getAttributeValue("", "from");
        final String reason;
        XmlPullParser.Event event = parser.next();
        switch (event) {
        case TEXT_CHARACTERS:
            reason = parser.getText();
            parser.next();
            break;
        case END_ELEMENT:
            reason = null;
            break;
        default:
            // TODO: Should be SmackParseException.
            throw new IOException("Unexpected event: " + event);
        }

        Date stamp = parseDate(stampString);
        return new DelayInformation(stamp, from, reason);
    }

    protected abstract Date parseDate(String string) throws SmackTextParseException;
}
