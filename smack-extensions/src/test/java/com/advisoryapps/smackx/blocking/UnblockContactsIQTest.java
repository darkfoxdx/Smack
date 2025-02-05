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
package com.advisoryapps.smackx.blocking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.StreamOpen;
import com.advisoryapps.smack.util.PacketParserUtils;

import com.advisoryapps.smackx.blocking.element.UnblockContactsIQ;

import org.junit.Test;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

public class UnblockContactsIQTest {

    private static final String unblockContactIQExample = "<iq id='unblock1' type='set'>" + "<unblock xmlns='urn:xmpp:blocking'>"
            + "<item jid='romeo@montague.net'/>" + "<item jid='pepe@montague.net'/>" + "</unblock>" + "</iq>";

    private static final String unblockContactPushIQExample = "<iq to='juliet@capulet.com/chamber' type='set' id='push3'>"
            + "<unblock xmlns='urn:xmpp:blocking'>" + "<item jid='romeo@montague.net'/>"
            + "<item jid='pepe@montague.net'/>" + "</unblock>" + "</iq>";

    private static final String unblockAllIQExample = "<iq id='unblock2' type='set'>" + "<unblock xmlns='urn:xmpp:blocking'/>"
            + "</iq>";

    private static final String unblockAllPushIQExample = "<iq to='juliet@capulet.com/chamber' type='set' id='push5'>"
            + "<unblock xmlns='urn:xmpp:blocking'/>" + "</iq>";

    @Test
    public void checkUnblockContactIQStanza() throws Exception {
        List<Jid> jids = new ArrayList<>();
        jids.add(JidCreate.from("romeo@montague.net"));
        jids.add(JidCreate.from("pepe@montague.net"));

        UnblockContactsIQ unblockContactIQ = new UnblockContactsIQ(jids);
        unblockContactIQ.setStanzaId("unblock1");

        assertEquals(unblockContactIQExample, unblockContactIQ.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void checkUnblockContactPushIQ() throws Exception {
        IQ iq = PacketParserUtils.parseStanza(unblockContactPushIQExample);
        UnblockContactsIQ unblockContactIQ = (UnblockContactsIQ) iq;
        assertEquals(JidCreate.from("romeo@montague.net"), unblockContactIQ.getJids().get(0));
        assertEquals(JidCreate.from("pepe@montague.net"), unblockContactIQ.getJids().get(1));
    }

    @Test
    public void checkUnblockAllIQStanza() throws Exception {
        UnblockContactsIQ unblockAllIQ = new UnblockContactsIQ(null);
        unblockAllIQ.setStanzaId("unblock2");
        assertEquals(unblockAllIQExample, unblockAllIQ.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void checkUnblockAllPushIQ() throws Exception {
        IQ iq = PacketParserUtils.parseStanza(unblockAllPushIQExample);
        UnblockContactsIQ unblockAllIQ = (UnblockContactsIQ) iq;
        assertNull(unblockAllIQ.getJids());
    }

}
