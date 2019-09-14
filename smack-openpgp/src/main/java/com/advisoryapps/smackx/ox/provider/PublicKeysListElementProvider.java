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
package com.advisoryapps.smackx.ox.provider;

import java.io.IOException;
import java.util.Date;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException.SmackTextParseException;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.ox.element.PublicKeysListElement;

import org.pgpainless.key.OpenPgpV4Fingerprint;

public final class PublicKeysListElementProvider extends ExtensionElementProvider<PublicKeysListElement> {

    public static final PublicKeysListElementProvider TEST_INSTANCE = new PublicKeysListElementProvider();

    @Override
    public PublicKeysListElement parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackTextParseException {

        PublicKeysListElement.Builder builder = PublicKeysListElement.builder();

        while (true) {

            XmlPullParser.TagEvent tag = parser.nextTag();
            String name;

            switch (tag) {
                case START_ELEMENT:
                    name = parser.getName();
                    if (PublicKeysListElement.PubkeyMetadataElement.ELEMENT.equals(name)) {
                        String finger = parser.getAttributeValue(null,
                                PublicKeysListElement.PubkeyMetadataElement.ATTR_V4_FINGERPRINT);
                        String dt = parser.getAttributeValue(null,
                                PublicKeysListElement.PubkeyMetadataElement.ATTR_DATE);
                        OpenPgpV4Fingerprint fingerprint = new OpenPgpV4Fingerprint(finger);
                        Date date = ParserUtils.getDateFromXep82String(dt);
                        builder.addMetadata(new PublicKeysListElement.PubkeyMetadataElement(fingerprint, date));
                    }
                    break;

                case END_ELEMENT:
                    name = parser.getName();
                    if (name.equals(PublicKeysListElement.ELEMENT)) {
                        return builder.build();
                    }
                    break;

                default:
                    // Catch all for incomplete switch (MissingCasesInEnumSwitch) statement.
                    break;
            }
        }
    }
}
