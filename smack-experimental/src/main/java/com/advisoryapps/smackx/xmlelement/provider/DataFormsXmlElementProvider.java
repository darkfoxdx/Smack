/**
 *
 * Copyright 2019 Florian Schmaus
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
package com.advisoryapps.smackx.xmlelement.provider;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.advisoryapps.smack.packet.StandardExtensionElement;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.parsing.StandardExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;
import com.advisoryapps.smackx.xdata.provider.FormFieldChildElementProvider;
import com.advisoryapps.smackx.xmlelement.element.DataFormsXmlElement;

public class DataFormsXmlElementProvider extends FormFieldChildElementProvider<DataFormsXmlElement> {

    @Override
    public QName getQName() {
        return DataFormsXmlElement.QNAME;
    }

    @Override
    public DataFormsXmlElement parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment)
                    throws IOException, XmlPullParserException, SmackParsingException {
        XmlPullParser.TagEvent tagEvent = parser.nextTag();

        final StandardExtensionElement standardExtensionElement;
        if (tagEvent == XmlPullParser.TagEvent.START_ELEMENT) {
            standardExtensionElement = StandardExtensionElementProvider.INSTANCE.parse(parser);
        } else {
            standardExtensionElement = null;
        }

        return new DataFormsXmlElement(standardExtensionElement);
    }

}
