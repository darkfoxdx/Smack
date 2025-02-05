/**
 *
 * Copyright 2018 Paul Schaub.
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
package com.advisoryapps.smack.provider;

import static com.advisoryapps.smack.util.PacketParserUtils.parseElementText;

import java.io.IOException;

import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.util.ParserUtils;

import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

public class BodyElementProvider extends ExtensionElementProvider<Message.Body> {

    @Override
    public Message.Body parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        String xmlLang = ParserUtils.getXmlLang(parser);
        String body = parseElementText(parser);

        return new Message.Body(xmlLang, body);
    }
}
