/**
 *
 * Copyright 2003-2006 Jive Software.
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
package com.advisoryapps.smackx.si.provider;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.si.packet.StreamInitiation;
import com.advisoryapps.smackx.si.packet.StreamInitiation.File;
import com.advisoryapps.smackx.xdata.packet.DataForm;
import com.advisoryapps.smackx.xdata.provider.DataFormProvider;

import org.jxmpp.util.XmppDateTime;

/**
 * The StreamInitiationProvider parses StreamInitiation packets.
 *
 * @author Alexander Wenckus
 *
 */
public class StreamInitiationProvider extends IQProvider<StreamInitiation> {
    private static final Logger LOGGER = Logger.getLogger(StreamInitiationProvider.class.getName());

    @Override
    public StreamInitiation parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        boolean done = false;

        // si
        String id = parser.getAttributeValue("", "id");
        String mimeType = parser.getAttributeValue("", "mime-type");

        StreamInitiation initiation = new StreamInitiation();

        // file
        String name = null;
        String size = null;
        String hash = null;
        String date = null;
        String desc = null;
        boolean isRanged = false;

        // feature
        DataForm form = null;
        DataFormProvider dataFormProvider = new DataFormProvider();

        String elementName;
        String namespace;
        while (!done) {
            XmlPullParser.Event eventType = parser.next();
            elementName = parser.getName();
            namespace = parser.getNamespace();
            if (eventType == XmlPullParser.Event.START_ELEMENT) {
                if (elementName.equals("file")) {
                    name = parser.getAttributeValue("", "name");
                    size = parser.getAttributeValue("", "size");
                    hash = parser.getAttributeValue("", "hash");
                    date = parser.getAttributeValue("", "date");
                } else if (elementName.equals("desc")) {
                    desc = parser.nextText();
                } else if (elementName.equals("range")) {
                    isRanged = true;
                } else if (elementName.equals("x")
                        && namespace.equals("jabber:x:data")) {
                    form = dataFormProvider.parse(parser);
                }
            } else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (elementName.equals("si")) {
                    done = true;
                } else if (elementName.equals("file")) {
                    long fileSize = 0;
                    if (size != null && size.trim().length() != 0) {
                        try {
                            fileSize = Long.parseLong(size);
                        }
                        catch (NumberFormatException e) {
                            LOGGER.log(Level.SEVERE, "Failed to parse file size from " + fileSize, e);
                        }
                    }

                    Date fileDate = new Date();
                    if (date != null) {
                        try {
                            fileDate = XmppDateTime.parseDate(date);
                        } catch (ParseException e) {
                            // couldn't parse date, use current date-time
                        }
                    }

                    File file = new File(name, fileSize);
                    file.setHash(hash);
                    file.setDate(fileDate);
                    file.setDesc(desc);
                    file.setRanged(isRanged);
                    initiation.setFile(file);
                }
            }
        }

        initiation.setSessionID(id);
        initiation.setMimeType(mimeType);

        initiation.setFeatureNegotiationForm(form);

        return initiation;
    }

}
