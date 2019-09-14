/**
 *
 * Copyright 2014-2019 Florian Schmaus
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
package com.advisoryapps.smackx.json.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.json.packet.AbstractJsonPacketExtension;

public abstract class AbstractJsonExtensionProvider<J extends AbstractJsonPacketExtension> extends ExtensionElementProvider<J> {

    @Override
    public J parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException,
                    IOException {
        String json = PacketParserUtils.parseElementText(parser);
        return from(json);
    }

    public abstract J from(String json);
}
