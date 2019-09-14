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

package com.advisoryapps.smackx.workgroup.packet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.workgroup.MetaData;
import com.advisoryapps.smackx.workgroup.util.MetaDataUtils;

/**
 * This provider parses meta data if it's not contained already in a larger extension provider.
 *
 * @author loki der quaeler
 */
public class MetaDataProvider extends ExtensionElementProvider<MetaData> {

    /**
     * PacketExtensionProvider implementation.
     * @throws IOException if an I/O error occured.
     * @throws XmlPullParserException if an error in the XML parser occured.
     */
    @Override
    public MetaData parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        Map<String, List<String>> metaData = MetaDataUtils.parseMetaData(parser);

        return new MetaData(metaData);
    }
}
