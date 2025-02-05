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
package com.advisoryapps.smackx.mediaelement.provider;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import com.advisoryapps.smack.datatypes.UInt16;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException.SmackUriSyntaxParsingException;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.mediaelement.element.MediaElement;
import com.advisoryapps.smackx.xdata.provider.FormFieldChildElementProvider;

public class MediaElementProvider extends FormFieldChildElementProvider<MediaElement> {

    private static final Logger LOGGER = Logger.getLogger(MediaElementProvider.class.getName());

    @Override
    public QName getQName() {
        return MediaElement.QNAME;
    }

    @Override
    public MediaElement parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws IOException, XmlPullParserException, SmackUriSyntaxParsingException {
        UInt16 height = ParserUtils.getUInt16Attribute(parser, "height");
        UInt16 width = ParserUtils.getUInt16Attribute(parser, "width");

        MediaElement.Builder mediaElementBuilder = MediaElement.builder();
        if (height != null && width != null) {
            mediaElementBuilder.setHeightAndWidth(height, width);
        } else if (height != null || width != null) {
            LOGGER.warning("Only one of height and width set while parsing media element");
        }

        outerloop: while (true) {
            XmlPullParser.TagEvent event = parser.nextTag();
            switch (event) {
            case START_ELEMENT:
                QName qname = parser.getQName();
                if (qname.equals(MediaElement.Uri.QNAME)) {
                    MediaElement.Uri uri = parseUri(parser);
                    mediaElementBuilder.addUri(uri);
                }
                break;
            case END_ELEMENT:
                if (parser.getDepth() == initialDepth) {
                    break outerloop;
                }
                break;
            }
        }

        return mediaElementBuilder.build();
    }

    private static MediaElement.Uri parseUri(XmlPullParser parser)
                    throws SmackUriSyntaxParsingException, XmlPullParserException, IOException {
        String type = parser.getAttributeValue("type");
        URI uri = ParserUtils.getUriFromNextText(parser);
        return new MediaElement.Uri(uri, type);
    }
}
