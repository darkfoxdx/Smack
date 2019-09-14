/**
 *
 * Copyright © 2017 Paul Schaub, 2019 Florian Schmaus
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
package com.advisoryapps.smackx.hashes.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.hashes.HashManager;
import com.advisoryapps.smackx.hashes.element.HashElement;

/**
 * Provider for HashElements.
 */
public class HashElementProvider extends ExtensionElementProvider<HashElement> {

    public static final HashElementProvider INSTANCE = new HashElementProvider();

    @Override
    public HashElement parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        String algo = parser.getAttributeValue(null, HashElement.ATTR_ALGO);
        String hashB64 = parser.nextText();
        return new HashElement(HashManager.ALGORITHM.valueOfName(algo), hashB64);
    }
}
