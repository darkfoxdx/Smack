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
package com.advisoryapps.smackx.xdata.provider;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.xdata.FormField;

public class OptionProvider extends FormFieldChildElementProvider<FormField.Option> {

    @Override
    public FormField.Option parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment)
                    throws XmlPullParserException, IOException, SmackParsingException {
        FormField.Option option = null;
        String label = parser.getAttributeValue("", "label");
        outerloop: while (true) {
            XmlPullParser.TagEvent eventType = parser.nextTag();
            switch (eventType) {
            case START_ELEMENT:
                String name = parser.getName();
                switch (name) {
                case "value":
                    option = new FormField.Option(label, parser.nextText());
                    break;
                }
                break;
            case END_ELEMENT:
                if (parser.getDepth() == initialDepth) {
                    break outerloop;
                }
                break;
            }
        }
        return option;
    }

    @Override
    public QName getQName() {
        return FormField.Option.QNAME;
    }

}
