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

package com.advisoryapps.smackx.workgroup.ext.forms;

import java.io.IOException;

import com.advisoryapps.smack.packet.SimpleIQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

public class WorkgroupForm extends SimpleIQ {

    /**
     * Element name of the stanza extension.
     */
    public static final String ELEMENT_NAME = "workgroup-form";

    /**
     * Namespace of the stanza extension.
     */
    public static final String NAMESPACE = "http://jivesoftware.com/protocol/workgroup";

    public WorkgroupForm() {
        super(ELEMENT_NAME, NAMESPACE);
    }

    /**
     * An IQProvider for WebForm packets.
     *
     * @author Derek DeMoro
     */
    public static class InternalProvider extends IQProvider<WorkgroupForm> {

        @Override
        public WorkgroupForm parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
            WorkgroupForm answer = new WorkgroupForm();

            boolean done = false;
            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT) {
                    // Parse the packet extension
                    PacketParserUtils.addExtensionElement(answer, parser, xmlEnvironment);
                }
                else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                    if (parser.getName().equals(ELEMENT_NAME)) {
                        done = true;
                    }
                }
            }

            return answer;
        }
    }
}
