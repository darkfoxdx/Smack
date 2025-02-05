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
package com.advisoryapps.smackx.muclight;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.StreamOpen;
import com.advisoryapps.smack.util.PacketParserUtils;

import com.advisoryapps.smackx.muclight.element.MUCLightAffiliationsIQ;
import com.advisoryapps.smackx.muclight.element.MUCLightGetAffiliationsIQ;

import org.junit.jupiter.api.Test;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

public class MUCLightGetAffiliationsTest {

    private static final String getAffiliationsIQExample = "<iq to='coven@muclight.shakespeare.lit' id='getmembers' type='get'>"
            + "<query xmlns='urn:xmpp:muclight:0#affiliations'>" + "<version>abcdefg</version>" + "</query>" + "</iq>";

    private static final String getAffiliationsResponseExample = "<iq from='coven@muclight.shakespeare.lit' id='getmembers' to='crone1@shakespeare.lit/desktop' type='result'>"
            + "<query xmlns='urn:xmpp:muclight:0#affiliations'>" + "<version>123456</version>"
            + "<user affiliation='owner'>user1@shakespeare.lit</user>"
            + "<user affiliation='member'>user2@shakespeare.lit</user>"
            + "<user affiliation='member'>user3@shakespeare.lit</user>" + "</query>" + "</iq>";

    @Test
    public void checkGetAffiliationsIQ() throws Exception {
        MUCLightGetAffiliationsIQ mucLightGetAffiliationsIQ = new MUCLightGetAffiliationsIQ(
                JidCreate.from("coven@muclight.shakespeare.lit"), "abcdefg");
        mucLightGetAffiliationsIQ.setStanzaId("getmembers");
        assertEquals(getAffiliationsIQExample, mucLightGetAffiliationsIQ.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void checkGetAffiliationsResponse() throws Exception {
        IQ iqInfoResult = PacketParserUtils.parseStanza(getAffiliationsResponseExample);
        MUCLightAffiliationsIQ mucLightAffiliationsIQ = (MUCLightAffiliationsIQ) iqInfoResult;

        assertEquals("123456", mucLightAffiliationsIQ.getVersion());

        HashMap<Jid, MUCLightAffiliation> affiliations = mucLightAffiliationsIQ.getAffiliations();
        assertEquals(3, affiliations.size());
        assertEquals(MUCLightAffiliation.owner, affiliations.get(JidCreate.from("user1@shakespeare.lit")));
        assertEquals(MUCLightAffiliation.member, affiliations.get(JidCreate.from("user2@shakespeare.lit")));
        assertEquals(MUCLightAffiliation.member, affiliations.get(JidCreate.from("user3@shakespeare.lit")));
    }

}
