/**
 *
 * Copyright 2014 Georg Lukas.
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
package com.advisoryapps.smackx.iqversion;

import static com.advisoryapps.smack.test.util.CharSequenceEquals.equalsCharSequence;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.advisoryapps.smack.DummyConnection;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.Stanza;
import com.advisoryapps.smack.util.PacketParserUtils;

import com.advisoryapps.smackx.InitExtensions;
import com.advisoryapps.smackx.iqversion.packet.Version;

import org.junit.Test;

public class VersionTest extends InitExtensions {
    @Test
    public void checkProvider() throws Exception {
        // @formatter:off
        String control = "<iq from='capulet.lit' to='juliet@capulet.lit/balcony' id='s2c1' type='get'>"
                + "<query xmlns='jabber:iq:version'/>"
                + "</iq>";
        // @formatter:on
        DummyConnection con = new DummyConnection();
        con.connect();

        // Enable version replys for this connection
        VersionManager.setAutoAppendSmackVersion(false);
        VersionManager.getInstanceFor(con).setVersion("Test", "0.23", "DummyOS");
        IQ versionRequest = PacketParserUtils.parseStanza(control);

        assertTrue(versionRequest instanceof Version);

        con.processStanza(versionRequest);

        Stanza replyPacket = con.getSentPacket();
        assertTrue(replyPacket instanceof Version);

        Version reply = (Version) replyPacket;
        // getFrom check is pending for SMACK-547
        // assertEquals("juliet@capulet.lit/balcony", reply.getFrom());
        assertThat("capulet.lit", equalsCharSequence(reply.getTo()));
        assertEquals("s2c1", reply.getStanzaId());
        assertEquals(IQ.Type.result, reply.getType());
        assertEquals("Test", reply.getName());
        assertEquals("0.23", reply.getVersion());
        assertEquals("DummyOS", reply.getOs());
    }
}
