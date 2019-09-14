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
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.mam.element.MamQueryIQ;
import com.advisoryapps.smackx.xdata.packet.DataForm;
import com.advisoryapps.smackx.xdata.provider.DataFormProvider;

/**
 * MAM Query IQ Provider class.
 *
 * @see <a href="http://xmpp.org/extensions/xep-0313.html">XEP-0313: Message
 *      Archive Management</a>
 * @author Fernando Ramirez
 *
 */
public class MamQueryIQProvider extends IQProvider<MamQueryIQ> {

    @Override
    public MamQueryIQ parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment)
                    throws XmlPullParserException, IOException, SmackParsingException {
        DataForm dataForm = null;
        String queryId = parser.getAttributeValue("", "queryid");
        String node = parser.getAttributeValue("", "node");

        outerloop: while (true) {
            final XmlPullParser.Event eventType = parser.next();
            final String name = parser.getName();

            switch (eventType) {
            case START_ELEMENT:
                switch (name) {
                case DataForm.ELEMENT:
                    dataForm = DataFormProvider.INSTANCE.parse(parser);
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

        return new MamQueryIQ(queryId, node, dataForm);
    }

}
