/**
 *
 * Copyright (C) 2007 Jive Software.
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
package com.advisoryapps.smack.packet;

import static com.advisoryapps.smack.test.util.XmlUnitUtils.assertXmlSimilar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class PresenceTest {
    @Test
    public void setPresenceTypeTest() throws IOException, SAXException {
        Presence.Type type = Presence.Type.unavailable;
        Presence.Type type2 = Presence.Type.subscribe;

        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<presence")
                .append(" type=\"")
                .append(type)
                .append("\">")
                .append("</presence>");
        String control = controlBuilder.toString();

        Presence presenceTypeInConstructor = new Presence(type);
        presenceTypeInConstructor.setStanzaId(null);
        assertEquals(type, presenceTypeInConstructor.getType());
        assertXmlSimilar(control, presenceTypeInConstructor.toXML(StreamOpen.CLIENT_NAMESPACE).toString());

        controlBuilder = new StringBuilder();
        controlBuilder.append("<presence")
                .append(" type=\"")
                .append(type2)
                .append("\">")
                .append("</presence>");
        control = controlBuilder.toString();

        Presence presenceTypeSet = getNewPresence();
        presenceTypeSet.setType(type2);
        assertEquals(type2, presenceTypeSet.getType());
        assertXmlSimilar(control, presenceTypeSet.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void setNullPresenceTypeTest() {
        assertThrows(IllegalArgumentException.class, () ->
        getNewPresence().setType(null)
        );
    }

    @Test
    public void isPresenceAvailableTest() {
        Presence presence = getNewPresence();
        presence.setType(Presence.Type.available);
        assertTrue(presence.isAvailable());

        presence.setType(Presence.Type.unavailable);
        assertFalse(presence.isAvailable());
    }

    @Test
    public void setPresenceStatusTest() throws IOException, SAXException {
        final String status = "This is a test of the emergency broadcast system.";

        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<presence>")
                .append("<status>")
                .append(status)
                .append("</status>")
                .append("</presence>");
        String control = controlBuilder.toString();

        Presence presence = getNewPresence();
        presence.setStatus(status);

        assertEquals(status, presence.getStatus());
        assertXmlSimilar(control, presence.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void setPresencePriorityTest() throws IOException, SAXException {
        final int priority = 10;

        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<presence>")
                .append("<priority>")
                .append(priority)
                .append("</priority>")
                .append("</presence>");
        String control = controlBuilder.toString();

        Presence presence = getNewPresence();
        presence.setPriority(priority);

        assertEquals(priority, presence.getPriority());
        assertXmlSimilar(control, presence.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void setIllegalPriorityTest() {
        assertThrows(IllegalArgumentException.class, () ->
        getNewPresence().setPriority(Integer.MIN_VALUE)
        );
    }

    @Test
    public void setPresenceModeTest() throws IOException, SAXException {
        Presence.Mode mode1 = Presence.Mode.dnd;
                final int priority = 10;
        final String status = "This is a test of the emergency broadcast system.";
        Presence.Mode mode2 = Presence.Mode.chat;

        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<presence>")
                .append("<status>")
                .append(status)
                .append("</status>")
                .append("<priority>")
                .append(priority)
                .append("</priority>")
                .append("<show>")
                .append(mode1)
                .append("</show>")
                .append("</presence>");
        String control = controlBuilder.toString();

        Presence presenceModeInConstructor = new Presence(Presence.Type.available, status, priority,
                mode1);
        presenceModeInConstructor.setStanzaId(null);
        assertEquals(mode1, presenceModeInConstructor.getMode());
        assertXmlSimilar(control, presenceModeInConstructor.toXML(StreamOpen.CLIENT_NAMESPACE).toString());

        controlBuilder = new StringBuilder();
        controlBuilder.append("<presence>")
                .append("<show>")
                .append(mode2)
                .append("</show>")
                .append("</presence>");
       control = controlBuilder.toString();

        Presence presenceModeSet = getNewPresence();
        presenceModeSet.setMode(mode2);
        assertEquals(mode2, presenceModeSet.getMode());
        assertXmlSimilar(control, presenceModeSet.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void isModeAwayTest() {
        Presence presence = getNewPresence();
        presence.setMode(Presence.Mode.away);
        assertTrue(presence.isAway());

        presence.setMode(Presence.Mode.chat);
        assertFalse(presence.isAway());
    }

    @Test
    public void presenceXmlLangTest() throws IOException, SAXException {
        final String lang = "sp";

        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<presence")
                .append(" xml:lang=\"")
                .append(lang)
                .append("\">")
                .append("</presence>");
        String control = controlBuilder.toString();

        Presence presence = getNewPresence();
        presence.setLanguage(lang);

        assertXmlSimilar(control, presence.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    private static Presence getNewPresence() {
        Presence presence = new Presence(Presence.Type.available);
        presence.setStanzaId(null);
        return presence;
    }
}
