/**
 *
 * Copyright 2014-2018 Florian Schmaus
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
package com.advisoryapps.smackx.shim.provider;

import static org.junit.Assert.assertEquals;

import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.test.util.TestUtils;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;

import com.advisoryapps.smackx.shim.packet.Header;
import com.advisoryapps.smackx.shim.packet.HeadersExtension;

import org.junit.Test;

public class HeadersProviderTest {

    @Test
    public void headersInMessageTest() throws Exception {
        // @formatter:off
        final String messageStanza =
          "<message xmlns='jabber:client' from='romeo@shakespeare.lit/orchard' to='juliet@capulet.com' type='chat'>" +
            "<body>Wherefore are thou?!?</body>" +
            "<headers xmlns='http://jabber.org/protocol/shim'>" +
             "<header name='Urgency'>high</header>" +
            "</headers>" +
          "</message>";
        // @formatter:on
        XmlPullParser parser = TestUtils.getMessageParser(messageStanza);
        Message message = PacketParserUtils.parseMessage(parser);
        HeadersExtension headers = HeadersExtension.from(message);
        Header header = headers.getHeaders().get(0);
        assertEquals("Urgency", header.getName());
        assertEquals("high", header.getValue());
    }
}
