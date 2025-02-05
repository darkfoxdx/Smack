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
package com.advisoryapps.smackx.workgroup.settings;

import java.io.IOException;

import com.advisoryapps.smack.packet.SimpleIQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.StringUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

public class SearchSettings extends SimpleIQ {
    private String forumsLocation;
    private String kbLocation;

    public boolean isSearchEnabled() {
        return StringUtils.isNotEmpty(getForumsLocation()) && StringUtils.isNotEmpty(getKbLocation());
    }

    public String getForumsLocation() {
        return forumsLocation;
    }

    public void setForumsLocation(String forumsLocation) {
        this.forumsLocation = forumsLocation;
    }

    public String getKbLocation() {
        return kbLocation;
    }

    public void setKbLocation(String kbLocation) {
        this.kbLocation = kbLocation;
    }

    public boolean hasKB() {
        return StringUtils.isNotEmpty(getKbLocation());
    }

    public boolean hasForums() {
        return StringUtils.isNotEmpty(getForumsLocation());
    }


    /**
     * Element name of the stanza extension.
     */
    public static final String ELEMENT_NAME = "search-settings";

    /**
     * Namespace of the stanza extension.
     */
    public static final String NAMESPACE = "http://jivesoftware.com/protocol/workgroup";

    public SearchSettings() {
        super(ELEMENT_NAME, NAMESPACE);
    }

    /**
     * Stanza extension provider for AgentStatusRequest packets.
     */
    public static class InternalProvider extends IQProvider<SearchSettings> {

        @Override
        public SearchSettings parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
            SearchSettings settings = new SearchSettings();

            boolean done = false;
            String kb = null;
            String forums = null;

            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT && "forums".equals(parser.getName())) {
                    forums = parser.nextText();
                }
                else if ((eventType == XmlPullParser.Event.START_ELEMENT) && "kb".equals(parser.getName())) {
                    kb = parser.nextText();
                }
                else if (eventType == XmlPullParser.Event.END_ELEMENT && "search-settings".equals(parser.getName())) {
                    done = true;
                }
            }

            settings.setForumsLocation(forums);
            settings.setKbLocation(kb);
            return settings;
        }
    }
}
