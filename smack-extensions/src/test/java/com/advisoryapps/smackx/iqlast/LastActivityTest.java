/**
 *
 * Copyright 2014 Florian Schmaus
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
package com.advisoryapps.smackx.iqlast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.advisoryapps.smack.DummyConnection;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.Stanza;
import com.advisoryapps.smack.util.PacketParserUtils;

import com.advisoryapps.smackx.InitExtensions;
import com.advisoryapps.smackx.iqlast.packet.LastActivity;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Test;

public class LastActivityTest extends InitExtensions {

    @Test
    public void checkProvider() throws Exception {
        XMLBuilder xml = XMLBuilder.create("iq");
        xml.a("from", "romeo@montague.net/orchard")
            .a("id", "last2")
            .a("to", "juliet@capulet.com/balcony")
            .a("type", "get")
            .e("query")
                .namespace(LastActivity.NAMESPACE);

        DummyConnection c = new DummyConnection();
        c.connect();
        IQ lastRequest = PacketParserUtils.parseStanza(xml.asString());
        assertTrue(lastRequest instanceof LastActivity);

        c.processStanza(lastRequest);
        Stanza reply = c.getSentPacket();
        assertTrue(reply instanceof LastActivity);
        LastActivity l = (LastActivity) reply;
        assertEquals("last2", l.getStanzaId());
        assertEquals(IQ.Type.result, l.getType());
    }
}
