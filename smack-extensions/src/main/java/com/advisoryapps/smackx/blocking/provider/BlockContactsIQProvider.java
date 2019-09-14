/**
 *
 * Copyright 2016 Fernando Ramirez
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
package com.advisoryapps.smackx.blocking.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.blocking.element.BlockContactsIQ;

import org.jxmpp.jid.Jid;

/**
 * Block contact IQ provider class.
 *
 * @author Fernando Ramirez
 * @see <a href="http://xmpp.org/extensions/xep-0191.html">XEP-0191: Blocking
 *      Command</a>
 */
public class BlockContactsIQProvider extends IQProvider<BlockContactsIQ> {

    @Override
    public BlockContactsIQ parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        List<Jid> jids = new ArrayList<>();

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                if (parser.getName().equals("item")) {
                    Jid jid = ParserUtils.getJidAttribute(parser);
                    jids.add(jid);
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

        return new BlockContactsIQ(jids);
    }

}
