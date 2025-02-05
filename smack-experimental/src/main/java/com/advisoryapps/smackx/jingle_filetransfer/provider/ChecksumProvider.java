/**
 *
 * Copyright 2017 Paul Schaub
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
package com.advisoryapps.smackx.jingle_filetransfer.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.hashes.element.HashElement;
import com.advisoryapps.smackx.hashes.provider.HashElementProvider;
import com.advisoryapps.smackx.jingle.element.JingleContent;
import com.advisoryapps.smackx.jingle_filetransfer.element.Checksum;
import com.advisoryapps.smackx.jingle_filetransfer.element.JingleFileTransferChild;
import com.advisoryapps.smackx.jingle_filetransfer.element.Range;

/**
 * Provider for the Checksum element.
 */
public class ChecksumProvider extends ExtensionElementProvider<Checksum> {
    @Override
    public Checksum parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        JingleContent.Creator creator = null;
        String creatorString = parser.getAttributeValue(null, Checksum.ATTR_CREATOR);
        if (creatorString != null) {
            creator = JingleContent.Creator.valueOf(creatorString);
        }
        String name = parser.getAttributeValue(null, Checksum.ATTR_NAME);


        JingleFileTransferChild.Builder cb = JingleFileTransferChild.getBuilder();
        HashElement hashElement = null;
        Range range = null;

        boolean go = true;
        while (go) {
            XmlPullParser.TagEvent tag = parser.nextTag();
            String n = parser.getText();

            switch (tag) {
            case START_ELEMENT:
                switch (n) {
                    case HashElement.ELEMENT:
                        hashElement = new HashElementProvider().parse(parser);
                        break;

                    case Range.ELEMENT:
                        String offset = parser.getAttributeValue(null, Range.ATTR_OFFSET);
                        String length = parser.getAttributeValue(null, Range.ATTR_LENGTH);
                        int o = offset == null ? 0 : Integer.parseInt(offset);
                        int l = length == null ? -1 : Integer.parseInt(length);
                        range = new Range(o, l);
                }
                break;
            case END_ELEMENT:
                switch (n) {
                    case Range.ELEMENT:
                        if (hashElement != null && range != null) {
                            range = new Range(range.getOffset(), range.getLength(), hashElement);
                            hashElement = null;
                        }
                        break;

                    case JingleFileTransferChild.ELEMENT:
                        if (hashElement != null) {
                            cb.setHash(hashElement);
                        }
                        if (range != null) {
                            cb.setRange(range);
                        }
                        go = false;
                }
                break;
            }
        }
        return new Checksum(creator, name, cb.build());
    }
}
