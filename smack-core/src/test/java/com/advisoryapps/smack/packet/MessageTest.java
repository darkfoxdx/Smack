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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.xml.sax.SAXException;

public class MessageTest {

    @Test
    public void setMessageTypeTest() throws IOException, SAXException {
        Message.Type type = Message.Type.chat;
        Message.Type type2 = Message.Type.headline;

        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<message")
                .append(" type=\"")
                .append(type)
                .append("\">")
                .append("</message>");
        String control = controlBuilder.toString();

        Message messageTypeInConstructor = new Message(null, Message.Type.chat);
        messageTypeInConstructor.setStanzaId(null);
        assertEquals(type, messageTypeInConstructor.getType());
        assertXmlSimilar(control, messageTypeInConstructor.toXML(StreamOpen.CLIENT_NAMESPACE).toString());

        controlBuilder = new StringBuilder();
        controlBuilder.append("<message")
                .append(" type=\"")
                .append(type2)
                .append("\">")
                .append("</message>");
        control = controlBuilder.toString();

        Message messageTypeSet = getNewMessage();
        messageTypeSet.setType(type2);
        assertEquals(type2, messageTypeSet.getType());
        assertXmlSimilar(control, messageTypeSet.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test(expected = NullPointerException.class)
    public void setNullMessageBodyTest() {
        Message message = getNewMessage();
        message.addBody(null, null);
    }

    @Test
    public void setMessageSubjectTest() throws IOException, SAXException {
        final String messageSubject = "This is a test of the emergency broadcast system.";

        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<message>")
                .append("<subject>")
                .append(messageSubject)
                .append("</subject>")
                .append("</message>");
        String control = controlBuilder.toString();

        Message message = getNewMessage();
        message.setSubject(messageSubject);

        assertEquals(messageSubject, message.getSubject());
        assertXmlSimilar(control, message.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void oneMessageBodyTest() throws IOException, SAXException {
        final String messageBody = "This is a test of the emergency broadcast system.";

        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<message>")
                .append("<body>")
                .append(messageBody)
                .append("</body>")
                .append("</message>");
        String control = controlBuilder.toString();

        Message message = getNewMessage();
        message.setBody(messageBody);

        assertEquals(messageBody, message.getBody());
        assertXmlSimilar(control, message.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void multipleMessageBodiesTest() throws IOException, SAXException {
        final String messageBody1 = "This is a test of the emergency broadcast system, 1.";
        final String lang2 = "ru";
        final String messageBody2 = "This is a test of the emergency broadcast system, 2.";
        final String lang3 = "sp";
        final String messageBody3 = "This is a test of the emergency broadcast system, 3.";

        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<message>")
                .append("<body>")
                .append(messageBody1)
                .append("</body>")
                .append("<body xml:lang=\"")
                .append(lang2)
                .append("\">")
                .append(messageBody2)
                .append("</body>")
                .append("<body xml:lang=\"")
                .append(lang3)
                .append("\">")
                .append(messageBody3)
                .append("</body>")
                .append("</message>");
        String control = controlBuilder.toString();

        Message message = getNewMessage();
        message.addBody(null, messageBody1);
        message.addBody(lang2, messageBody2);
        message.addBody(lang3, messageBody3);
        assertXmlSimilar(control, message.toXML(StreamOpen.CLIENT_NAMESPACE));

        Collection<String> languages = message.getBodyLanguages();
        List<String> controlLanguages = new ArrayList<>();
        controlLanguages.add(lang2);
        controlLanguages.add(lang3);
        controlLanguages.removeAll(languages);
        assertTrue(controlLanguages.size() == 0);
    }

    @Test
    public void removeMessageBodyTest() {
        Message message = getNewMessage();
        message.setBody("test");
        assertTrue(message.getBodies().size() == 1);

        message.setBody(null);
        assertTrue(message.getBodies().size() == 0);

        assertFalse(message.removeBody("sp"));

        Message.Body body = message.addBody("es", "test");
        assertTrue(message.getBodies().size() == 1);

        message.removeBody(body);
        assertTrue(message.getBodies().size() == 0);
    }

    @Test
    public void setMessageThreadTest() throws IOException, SAXException {
        final String messageThread = "1234";

        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<message>")
                .append("<thread>")
                .append(messageThread)
                .append("</thread>")
                .append("</message>");
        String control = controlBuilder.toString();

        Message message = getNewMessage();
        message.setThread(messageThread);

        assertEquals(messageThread, message.getThread());
        assertXmlSimilar(control, message.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void messageXmlLangTest() throws IOException, SAXException {
        final String lang = "sp";

        StringBuilder controlBuilder = new StringBuilder();
        controlBuilder.append("<message")
                .append(" xml:lang=\"")
                .append(lang)
                .append("\">")
                .append("</message>");
        String control = controlBuilder.toString();

        Message message = getNewMessage();
        message.setLanguage(lang);

        assertXmlSimilar(control, message.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    private static Message getNewMessage() {
        Message message = new Message();
        message.setStanzaId(null);
        return message;
    }
}
