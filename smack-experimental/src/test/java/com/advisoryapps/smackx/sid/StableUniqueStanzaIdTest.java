/**
 *
 * Copyright 2018 Paul Schaub
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
package com.advisoryapps.smackx.sid;

import static com.advisoryapps.smack.test.util.XmlUnitUtils.assertXmlSimilar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.test.util.SmackTestSuite;
import com.advisoryapps.smack.test.util.TestUtils;
import com.advisoryapps.smack.util.PacketParserUtils;

import com.advisoryapps.smackx.sid.element.OriginIdElement;
import com.advisoryapps.smackx.sid.element.StanzaIdElement;
import com.advisoryapps.smackx.sid.provider.OriginIdProvider;
import com.advisoryapps.smackx.sid.provider.StanzaIdProvider;

import org.junit.jupiter.api.Test;

public class StableUniqueStanzaIdTest extends SmackTestSuite {

    @Test
    public void stanzaIdProviderTest() throws Exception {
        String xml = "<stanza-id xmlns='urn:xmpp:sid:0' id='de305d54-75b4-431b-adb2-eb6b9e546013' by='alice@wonderland.lit' />";
        StanzaIdElement element = new StanzaIdElement("de305d54-75b4-431b-adb2-eb6b9e546013", "alice@wonderland.lit");
        assertEquals("de305d54-75b4-431b-adb2-eb6b9e546013", element.getId());
        assertEquals("alice@wonderland.lit", element.getBy());
        assertXmlSimilar(xml, element.toXML().toString());

        StanzaIdElement parsed = StanzaIdProvider.INSTANCE.parse(TestUtils.getParser(xml));
        assertEquals(element.getId(), parsed.getId());
        assertEquals(element.getBy(), parsed.getBy());
    }

    @Test
    public void originIdProviderTest() throws Exception {
        String xml = "<origin-id xmlns='urn:xmpp:sid:0' id='de305d54-75b4-431b-adb2-eb6b9e546013' />";
        OriginIdElement element = new OriginIdElement("de305d54-75b4-431b-adb2-eb6b9e546013");
        assertEquals("de305d54-75b4-431b-adb2-eb6b9e546013", element.getId());
        assertXmlSimilar(xml, element.toXML().toString());

        OriginIdElement parsed = OriginIdProvider.INSTANCE.parse(TestUtils.getParser(xml));
        assertEquals(element.getId(), parsed.getId());
    }

    @Test
    public void createOriginIdTest() {
        OriginIdElement element = new OriginIdElement();
        assertNotNull(element);
        assertEquals(StableUniqueStanzaIdManager.NAMESPACE, element.getNamespace());
        assertEquals(16, element.getId().length());
    }

    @Test
    public void fromMessageTest() {
        Message message = new Message();
        assertFalse(OriginIdElement.hasOriginId(message));
        assertFalse(StanzaIdElement.hasStanzaId(message));

        OriginIdElement.addOriginId(message);

        assertTrue(OriginIdElement.hasOriginId(message));

        StanzaIdElement stanzaId = new StanzaIdElement("alice@wonderland.lit");
        message.addExtension(stanzaId);
        assertTrue(StanzaIdElement.hasStanzaId(message));
        assertEquals(stanzaId, StanzaIdElement.getStanzaId(message));
    }

    @Test
    public void testMultipleUssidExtensions() throws Exception {
        String message = "<message xmlns='jabber:client' from='e4aec989-3e20-4846-83bf-f50df89b5d07@muclight.example.com/user1@example.com' to='user1@example.com' id='6b71fe3a-3cb2-489c-9c8e-b6879761d15e' type='groupchat'>" +
                          "<body>Test message</body>" +
                          "<markable xmlns='urn:xmpp:chat-markers:0'/>" +
                          "<stanza-id by='e4aec989-3e20-4846-83bf-f50df89b5d07@muclight.example.com' id='B0KK24ETVC81' xmlns='urn:xmpp:sid:0'/>" +
                          "<stanza-id by='user1@example.com' id='B0KK24EV89G1' xmlns='urn:xmpp:sid:0'/>" +
                        "</message>";
        Message messageStanza = PacketParserUtils.parseStanza(message);

        assertTrue(StanzaIdElement.hasStanzaId(messageStanza));
    }
}
