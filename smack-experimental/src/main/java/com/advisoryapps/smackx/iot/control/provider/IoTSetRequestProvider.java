/**
 *
 * Copyright © 2016-2019 Florian Schmaus
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
package com.advisoryapps.smackx.iot.control.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.iot.control.element.IoTSetRequest;
import com.advisoryapps.smackx.iot.control.element.SetBoolData;
import com.advisoryapps.smackx.iot.control.element.SetData;
import com.advisoryapps.smackx.iot.control.element.SetDoubleData;
import com.advisoryapps.smackx.iot.control.element.SetIntData;
import com.advisoryapps.smackx.iot.control.element.SetLongData;

public class IoTSetRequestProvider extends IQProvider<IoTSetRequest> {

    @Override
    public IoTSetRequest parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        List<SetData> data = new ArrayList<>(4);
        outerloop: while (true) {
            final XmlPullParser.Event eventType = parser.next();
            final String name = parser.getName();
            switch (eventType) {
            case START_ELEMENT:
                switch (name) {
                case "bool": {
                    String valueName = parser.getAttributeValue(null, "name");
                    String valueString = parser.getAttributeValue(null, "value");
                    boolean value = Boolean.parseBoolean(valueString);
                    data.add(new SetBoolData(valueName, value));
                }
                    break;
                case "double": {
                    String valueName = parser.getAttributeValue(null, "name");
                    String valueString = parser.getAttributeValue(null, "value");
                    double value = Double.parseDouble(valueString);
                    data.add(new SetDoubleData(valueName, value));
                }
                    break;
                case "int": {
                    String valueName = parser.getAttributeValue(null, "name");
                    String valueString = parser.getAttributeValue(null, "value");
                    int value = Integer.parseInt(valueString);
                    data.add(new SetIntData(valueName, value));
                }
                    break;
                case "long": {
                    String valueName = parser.getAttributeValue(null, "name");
                    String valueString = parser.getAttributeValue(null, "value");
                    long value = Long.parseLong(valueString);
                    data.add(new SetLongData(valueName, value));
                }
                    break;
                }
                break;
            case END_ELEMENT:
                if (parser.getDepth() == initialDepth) {
                    break outerloop;
                }
                break;
            default:
                // Catch all for incomplete switch (MissingCasesInEnumSwitch) statement.
                break;
            }
        }
        return new IoTSetRequest(data);
    }

}
