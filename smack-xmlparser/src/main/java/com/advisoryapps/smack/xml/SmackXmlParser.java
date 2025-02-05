/**
 *
 * Copyright 2019 Florian Schmaus.
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
package com.advisoryapps.smack.xml;

import java.io.Reader;
import java.util.Iterator;
import java.util.ServiceLoader;

public class SmackXmlParser {

    private static final ServiceLoader<XmlPullParserFactory> xmlPullParserFactoryServiceLoader;

    static {
        xmlPullParserFactoryServiceLoader = ServiceLoader.load(XmlPullParserFactory.class);
    }

    public static XmlPullParserFactory getXmlPullParserFactory() {
        Iterator<XmlPullParserFactory> iterator = xmlPullParserFactoryServiceLoader.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalStateException("Could not load a XmlPullParserFactory via Service Provider Interface (SPI)");
        }
        return iterator.next();
    }

    /**
     * Creates a new XmlPullParser suitable for parsing XMPP. This means in particular that
     * FEATURE_PROCESS_NAMESPACES is enabled.
     * <p>
     * Note that not all XmlPullParser implementations will return a String on
     * <code>getText()</code> if the parser is on START_ELEMENT or END_ELEMENT. So you must not rely on this
     * behavior when using the parser.
     * </p>
     *
     * @param reader a reader to read the XML data from.
     * @return A suitable XmlPullParser for XMPP parsing.
     * @throws XmlPullParserException in case of an XmlPullParserException.
     */
    public static XmlPullParser newXmlParser(Reader reader) throws XmlPullParserException {
        XmlPullParserFactory xmlPullParserFactory = getXmlPullParserFactory();
        return xmlPullParserFactory.newXmlPullParser(reader);
    }

}
