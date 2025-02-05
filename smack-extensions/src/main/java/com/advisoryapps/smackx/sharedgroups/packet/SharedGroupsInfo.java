/**
 *
 * Copyright 2005 Jive Software.
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
package com.advisoryapps.smackx.sharedgroups.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

/**
 * IQ stanza used for discovering the user's shared groups and for getting the answer back
 * from the server.<p>
 *
 * Important note: This functionality is not part of the XMPP spec and it will only work
 * with Wildfire.
 *
 * @author Gaston Dombiak
 */
public class SharedGroupsInfo extends IQ {

    public static final String ELEMENT = "sharedgroup";
    public static final String NAMESPACE = "http://www.jivesoftware.org/protocol/sharedgroup";

    private final List<String> groups = new ArrayList<>();

    public SharedGroupsInfo() {
        super(ELEMENT, NAMESPACE);
    }

    /**
     * Returns a collection with the shared group names returned from the server.
     *
     * @return collection with the shared group names returned from the server.
     */
    public List<String> getGroups() {
        return groups;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        buf.rightAngleBracket();
        for (String group : groups) {
            buf.element("group", group);
        }
        return buf;
    }

    /**
     * Internal Search service Provider.
     */
    public static class Provider extends IQProvider<SharedGroupsInfo> {

        @Override
        public SharedGroupsInfo parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment)
                        throws XmlPullParserException, IOException {
            SharedGroupsInfo groupsInfo = new SharedGroupsInfo();

            boolean done = false;
            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT && parser.getName().equals("group")) {
                    groupsInfo.getGroups().add(parser.nextText());
                }
                else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                    if (parser.getName().equals("sharedgroup")) {
                        done = true;
                    }
                }
            }
            return groupsInfo;
        }
    }
}
