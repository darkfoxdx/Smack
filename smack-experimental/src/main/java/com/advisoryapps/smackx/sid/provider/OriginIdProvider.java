/**
 *
 * Copyright 2018 Paul Schaub
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
package com.advisoryapps.smackx.sid.provider;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;

import com.advisoryapps.smackx.sid.element.OriginIdElement;

public class OriginIdProvider extends ExtensionElementProvider<OriginIdElement> {

    public static final OriginIdProvider INSTANCE = new OriginIdProvider();

    // TODO: Remove in Smack 4.4.
    @Deprecated
    public static final OriginIdProvider TEST_INSTANCE = INSTANCE;

    @Override
    public OriginIdElement parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) {
        return new OriginIdElement(parser.getAttributeValue(null, OriginIdElement.ATTR_ID));
    }
}
