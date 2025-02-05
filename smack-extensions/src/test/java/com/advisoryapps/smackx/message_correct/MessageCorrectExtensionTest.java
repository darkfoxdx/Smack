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
package com.advisoryapps.smackx.message_correct;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.util.PacketParserUtils;

import com.advisoryapps.smackx.message_correct.element.MessageCorrectExtension;

import org.junit.Test;

public class MessageCorrectExtensionTest {

    private static final String idInitialMessage = "bad1";

    private static final String initialMessageXml = "<message xmlns='jabber:client' to='juliet@capulet.net/balcony' id='good1'>"
            + "<body>But soft, what light through yonder window breaks?</body>" + "</message>";

    private static final CharSequence messageCorrectionXml = "<replace xmlns='urn:xmpp:message-correct:0' id='bad1'/>";

    private static final CharSequence expectedXml = "<message xmlns='jabber:client' to='juliet@capulet.net/balcony' id='good1'>"
            + "<body>But soft, what light through yonder window breaks?</body>"
            + "<replace xmlns='urn:xmpp:message-correct:0' id='bad1'/>" + "</message>";

    @Test
    public void checkStanzas() throws Exception {
        Message initialMessage = PacketParserUtils.parseStanza(initialMessageXml);
        MessageCorrectExtension messageCorrectExtension = new MessageCorrectExtension(idInitialMessage);

        assertEquals(messageCorrectExtension.toXML().toString(), messageCorrectionXml.toString());

        initialMessage.addExtension(messageCorrectExtension);

        assertEquals(initialMessage.toXML().toString(), expectedXml.toString());
    }

}
