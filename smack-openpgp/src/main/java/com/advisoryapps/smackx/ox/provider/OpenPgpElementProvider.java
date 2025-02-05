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
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.ox.element.OpenPgpElement;

/**
 * {@link ExtensionElementProvider} implementation for the {@link OpenPgpElement}.
 */
public class OpenPgpElementProvider extends ExtensionElementProvider<OpenPgpElement> {

    public static final OpenPgpElementProvider TEST_INSTANCE = new OpenPgpElementProvider();

    @Override
    public OpenPgpElement parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        String base64EncodedOpenPgpMessage = parser.nextText();
        return new OpenPgpElement(base64EncodedOpenPgpMessage);
    }

}
