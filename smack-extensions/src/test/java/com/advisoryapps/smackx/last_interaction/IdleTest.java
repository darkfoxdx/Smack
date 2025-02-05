/**
 *
 * Copyright © 2018 Paul Schaub
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
package com.advisoryapps.smackx.last_interaction;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static com.advisoryapps.smack.test.util.XmlUnitUtils.assertXmlSimilar;

import java.util.Date;

import com.advisoryapps.smack.packet.Presence;
import com.advisoryapps.smack.test.util.SmackTestSuite;
import com.advisoryapps.smack.test.util.TestUtils;
import com.advisoryapps.smack.xml.XmlPullParser;

import com.advisoryapps.smackx.last_interaction.element.IdleElement;
import com.advisoryapps.smackx.last_interaction.provider.IdleProvider;

import org.junit.Test;
import org.jxmpp.util.XmppDateTime;

public class IdleTest extends SmackTestSuite {

    @Test
    public void providerTest() throws Exception {
        String xml = "<idle xmlns='urn:xmpp:idle:1' since='1969-07-21T02:56:15Z' />";
        XmlPullParser parser = TestUtils.getParser(xml);
        assertNotNull(parser);
        IdleElement parsed = IdleProvider.TEST_INSTANCE.parse(parser);
        Date date = XmppDateTime.parseXEP0082Date("1969-07-21T02:56:15Z");
        assertEquals(date, parsed.getSince());

        IdleElement element = new IdleElement(date);
        assertXmlSimilar("<idle xmlns='urn:xmpp:idle:1' since='1969-07-21T02:56:15.000+00:00'/>", element.toXML().toString());
    }

    @Test
    public void helperTest() {
        Presence presence = new Presence(Presence.Type.available);
        IdleElement.addToPresence(presence);
        IdleElement element = IdleElement.fromPresence(presence);
        assertNotNull(element);
    }
}
