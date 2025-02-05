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
package com.advisoryapps.smackx.mam.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.mam.element.MamFinIQ;
import com.advisoryapps.smackx.rsm.packet.RSMSet;
import com.advisoryapps.smackx.rsm.provider.RSMSetProvider;

/**
 * MAM Fin IQ Provider class.
 *
 * @see <a href="http://xmpp.org/extensions/xep-0313.html">XEP-0313: Message
 *      Archive Management</a>
 * @author Fernando Ramirez
 *
 */
public class MamFinIQProvider extends IQProvider<MamFinIQ> {

    @Override
    public MamFinIQ parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        String queryId = parser.getAttributeValue("", "queryid");
        boolean complete = ParserUtils.getBooleanAttribute(parser, "complete", false);
        boolean stable = ParserUtils.getBooleanAttribute(parser, "stable", true);
        RSMSet rsmSet = null;

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                if (parser.getName().equals(RSMSet.ELEMENT)) {
                    rsmSet = RSMSetProvider.INSTANCE.parse(parser);
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

        return new MamFinIQ(queryId, rsmSet, complete, stable);
    }

}
