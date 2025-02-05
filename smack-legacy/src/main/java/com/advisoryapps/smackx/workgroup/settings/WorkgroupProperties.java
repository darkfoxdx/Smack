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

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.StringUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

public class WorkgroupProperties extends IQ {

    private boolean authRequired;
    private String email;
    private String fullName;
    private String jid;

    public boolean isAuthRequired() {
        return authRequired;
    }

    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }


    /**
     * Element name of the stanza extension.
     */
    public static final String ELEMENT_NAME = "workgroup-properties";

    /**
     * Namespace of the stanza extension.
     */
    public static final String NAMESPACE = "http://jivesoftware.com/protocol/workgroup";

    public WorkgroupProperties() {
        super(ELEMENT_NAME, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        if (StringUtils.isNotEmpty(getJid())) {
            buf.append("jid=\"" + getJid() + "\" ");
        }
        buf.setEmptyElement();
        return buf;
    }

    /**
     * Stanza extension provider for SoundSetting Packets.
     */
    public static class InternalProvider extends IQProvider<WorkgroupProperties> {

        @Override
        public WorkgroupProperties parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
            WorkgroupProperties props = new WorkgroupProperties();

            boolean done = false;


            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT && "authRequired".equals(parser.getName())) {
                    // CHECKSTYLE:OFF
                    props.setAuthRequired(Boolean.valueOf(parser.nextText()).booleanValue());
                    // CHECKSTYLE:ON
                }
                else if (eventType == XmlPullParser.Event.START_ELEMENT && "email".equals(parser.getName())) {
                    props.setEmail(parser.nextText());
                }
                else if (eventType == XmlPullParser.Event.START_ELEMENT && "name".equals(parser.getName())) {
                    props.setFullName(parser.nextText());
                }
                else if (eventType == XmlPullParser.Event.END_ELEMENT && "workgroup-properties".equals(parser.getName())) {
                    done = true;
                }
            }

            return props;
        }
    }
}
