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
package com.advisoryapps.smackx.spoiler.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.spoiler.element.SpoilerElement;

public class SpoilerProvider extends ExtensionElementProvider<SpoilerElement> {

    public static SpoilerProvider INSTANCE = new SpoilerProvider();

    @Override
    public SpoilerElement parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        String lang = ParserUtils.getXmlLang(parser);
        String hint = null;

        outerloop: while (true) {
            XmlPullParser.Event tag = parser.next();
            switch (tag) {
                case TEXT_CHARACTERS:
                    hint = parser.getText();
                    break;
                case END_ELEMENT:
                    break outerloop;
                default:
                    // Catch all for incomplete switch (MissingCasesInEnumSwitch) statement.
                    break;
            }
        }
        return new SpoilerElement(lang, hint);
    }
}
