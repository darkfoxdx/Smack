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
package com.advisoryapps.smackx.bob;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import com.advisoryapps.smack.packet.IQ.Type;
import com.advisoryapps.smack.packet.StreamOpen;
import com.advisoryapps.smack.test.util.SmackTestSuite;
import com.advisoryapps.smack.util.PacketParserUtils;

import com.advisoryapps.smackx.bob.element.BoBIQ;

import org.junit.Test;
import org.jxmpp.jid.impl.JidCreate;

public class BoBIQTest extends SmackTestSuite {

    private static final String sampleBoBIQRequest = "<iq to='ladymacbeth@shakespeare.lit/castle' id='sarasa' type='get'>"
            + "<data xmlns='urn:xmpp:bob' cid='sha1+8f35fef110ffc5df08d579a50083ff9308fb6242@bob.xmpp.org'/>" + "</iq>";

    private static final String sampleBoBIQResponse = "<iq to='doctor@shakespeare.lit/pda' id='sarasa' type='result'>"
            + "<data xmlns='urn:xmpp:bob' cid='sha1+8f35fef110ffc5df08d579a50083ff9308fb6242@bob.xmpp.org' "
            + "max-age='86400' type='image/png'>" + "c2FyYXNhZGUyMzU0ajI=" + "</data>" + "</iq>";

    @Test
    public void checkBoBIQRequest() throws Exception {
        BoBHash bobHash = new BoBHash("8f35fef110ffc5df08d579a50083ff9308fb6242", "sha1");

        BoBIQ createdBoBIQ = new BoBIQ(bobHash);
        createdBoBIQ.setStanzaId("sarasa");
        createdBoBIQ.setTo(JidCreate.from("ladymacbeth@shakespeare.lit/castle"));
        createdBoBIQ.setType(Type.get);

        assertEquals(sampleBoBIQRequest, createdBoBIQ.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void checkBoBIQResponse() throws Exception {
        BoBIQ bobIQ = PacketParserUtils.parseStanza(sampleBoBIQResponse);

        BoBHash bobHash = new BoBHash("8f35fef110ffc5df08d579a50083ff9308fb6242", "sha1");
        BoBData bobData = new BoBData("image/png", "sarasade2354j2".getBytes(StandardCharsets.UTF_8), 86400);

        BoBIQ createdBoBIQ = new BoBIQ(bobHash, bobData);
        createdBoBIQ.setStanzaId("sarasa");
        createdBoBIQ.setTo(JidCreate.from("doctor@shakespeare.lit/pda"));
        createdBoBIQ.setType(Type.result);

        assertEquals(bobIQ.getBoBHash().getHash(), createdBoBIQ.getBoBHash().getHash());
        assertEquals(bobIQ.getBoBHash().getHashType(), createdBoBIQ.getBoBHash().getHashType());
        assertEquals(bobIQ.getBoBData().getMaxAge(), createdBoBIQ.getBoBData().getMaxAge());
        assertEquals(bobIQ.getBoBData().getType(), createdBoBIQ.getBoBData().getType());
        assertEquals(bobIQ.getBoBData().getContentBase64Encoded(), createdBoBIQ.getBoBData().getContentBase64Encoded());
        assertEquals(bobIQ.toXML(StreamOpen.CLIENT_NAMESPACE).toString(), createdBoBIQ.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

}
