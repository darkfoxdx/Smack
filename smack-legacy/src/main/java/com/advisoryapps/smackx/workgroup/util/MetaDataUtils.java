/**
 *
 * Copyright 2003-2007 Jive Software.
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
package com.advisoryapps.smackx.workgroup.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.advisoryapps.smack.util.StringUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.workgroup.MetaData;

/**
 * Utility class for meta-data parsing and writing.
 *
 * @author Matt Tucker
 */
public class MetaDataUtils {

    /**
     * Parses any available meta-data and returns it as a Map of String name/value pairs. The
     * parser must be positioned at an opening meta-data tag, or the an empty map will be returned.
     *
     * @param parser the XML parser positioned at an opening meta-data tag.
     * @return the meta-data.
     * @throws XmlPullParserException if an error occurs while parsing the XML.
     * @throws IOException            if an error occurs while parsing the XML.
     */
    public static Map<String, List<String>> parseMetaData(XmlPullParser parser) throws XmlPullParserException, IOException {
        XmlPullParser.Event eventType = parser.getEventType();

        // If correctly positioned on an opening meta-data tag, parse meta-data.
        if ((eventType == XmlPullParser.Event.START_ELEMENT)
                && parser.getName().equals(MetaData.ELEMENT_NAME)
                && parser.getNamespace().equals(MetaData.NAMESPACE)) {
            Map<String, List<String>> metaData = new Hashtable<>();

            eventType = parser.next();

            // Keep parsing until we've gotten to end of meta-data.
            while ((eventType != XmlPullParser.Event.END_ELEMENT)
                    || !parser.getName().equals(MetaData.ELEMENT_NAME)) {
                String name = parser.getAttributeValue(0);
                String value = parser.nextText();

                if (metaData.containsKey(name)) {
                    List<String> values = metaData.get(name);
                    values.add(value);
                }
                else {
                    List<String> values = new ArrayList<>();
                    values.add(value);
                    metaData.put(name, values);
                }

                eventType = parser.next();
            }

            return metaData;
        }

        return Collections.emptyMap();
    }

    /**
     * Serializes a Map of String name/value pairs into the meta-data XML format.
     *
     * @param metaData the Map of meta-data as Map&lt;String, List&lt;String&gt;&gt;
     * @return the meta-data values in XML form.
     */
    public static String serializeMetaData(Map<String, List<String>> metaData) {
        StringBuilder buf = new StringBuilder();
        if (metaData != null && metaData.size() > 0) {
            buf.append("<metadata xmlns=\"http://jivesoftware.com/protocol/workgroup\">");
            for (String key : metaData.keySet()) {
                List<String> value = metaData.get(key);
                for (String v : value) {
                    buf.append("<value name=\"").append(key).append("\">");
                    buf.append(StringUtils.escapeForXmlText(v));
                    buf.append("</value>");
                }
            }
            buf.append("</metadata>");
        }
        return buf.toString();
    }
}
