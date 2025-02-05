/**
 *
 * Copyright 2017-2019 Florian Schmaus.
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
package com.advisoryapps.smackx.ox.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.ox.element.CryptElement;

/**
 * {@link com.advisoryapps.smack.provider.ExtensionElementProvider} implementation for the {@link CryptElement}.
 */
public class CryptElementProvider extends OpenPgpContentElementProvider<CryptElement> {

    public static final CryptElementProvider INSTANCE = new CryptElementProvider();

    @Override
    public CryptElement parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        OpenPgpContentElementData data = parseOpenPgpContentElementData(parser, initialDepth);

        return new CryptElement(data.to, data.rpad, data.timestamp, data.payload);
    }

}
