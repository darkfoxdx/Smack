/**
 *
 * Copyright 2014 Anno van Vliet
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
package com.advisoryapps.smackx.xdatavalidation.provider;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import com.advisoryapps.smack.datatypes.UInt32;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.xdata.provider.FormFieldChildElementProvider;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement.BasicValidateElement;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement.ListRange;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement.OpenValidateElement;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement.RangeValidateElement;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement.RegexValidateElement;

/**
 * Extension Provider for Data validation of forms.
 *
 * @author Anno van Vliet
 *
 */
public class DataValidationProvider extends FormFieldChildElementProvider<ValidateElement> {
    private static final Logger LOGGER = Logger.getLogger(DataValidationProvider.class.getName());

    @Override
    public ValidateElement parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment)
                    throws XmlPullParserException, IOException {
        final String dataType = parser.getAttributeValue("", "datatype");
        ValidateElement dataValidation = null;
        ListRange listRange = null;

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                String name = parser.getName();
                switch (name) {
                case OpenValidateElement.METHOD:
                    dataValidation = new OpenValidateElement(dataType);
                    break;
                case BasicValidateElement.METHOD:
                    dataValidation = new BasicValidateElement(dataType);
                    break;
                case RangeValidateElement.METHOD:
                    dataValidation = new RangeValidateElement(dataType,
                                    parser.getAttributeValue("", "min"),
                                    parser.getAttributeValue("", "max")
                                    );
                    break;
                case RegexValidateElement.METHOD:
                    dataValidation = new RegexValidateElement(dataType, parser.nextText());
                    break;
                case ListRange.ELEMENT:
                    UInt32 min = ParserUtils.getUInt32Attribute(parser, "min");
                    UInt32 max = ParserUtils.getUInt32Attribute(parser, "max");
                    if (min != null || max != null) {
                        listRange = new ListRange(min, max);
                    } else {
                        LOGGER.fine("Ignoring list-range element without min or max attribute");
                    }
                    break;
                default:
                    break;
                }
                break;
            case END_ELEMENT:
                if (parser.getDepth() == initialDepth) {
                    if (dataValidation == null) {
                        // XEP-122 § 3.2 states that "If no validation method is specified,
                        // form processors MUST assume <basic/> validation."
                        dataValidation = new BasicValidateElement(dataType);
                    }
                    dataValidation.setListRange(listRange);
                    break outerloop;
                }
                break;
            default:
                // Catch all for incomplete switch (MissingCasesInEnumSwitch) statement.
                break;
            }
        }
        return dataValidation;
    }

    @Override
    public QName getQName() {
        return ValidateElement.QNAME;
    }

}
