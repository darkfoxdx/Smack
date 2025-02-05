/**
 *
 * Copyright 2003-2007 Jive Software, 2014-2019 Florian Schmaus
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
package com.advisoryapps.smackx.xhtmlim.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.xhtmlim.packet.XHTMLExtension;

/**
 * The XHTMLExtensionProvider parses XHTML packets.
 *
 * @author Florian Schmaus
 */
public class XHTMLExtensionProvider extends ExtensionElementProvider<XHTMLExtension> {

    @Override
    public XHTMLExtension parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws IOException, XmlPullParserException {
        XHTMLExtension xhtmlExtension = new XHTMLExtension();

        while (true) {
            XmlPullParser.Event eventType = parser.getEventType();
            String name = parser.getName();
            if (eventType == XmlPullParser.Event.START_ELEMENT) {
                if (name.equals(Message.BODY)) {
                    xhtmlExtension.addBody(PacketParserUtils.parseElement(parser));
                }
            } else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getDepth() == initialDepth) {
                    return xhtmlExtension;
                }
            }
            parser.next();
        }
    }
}
