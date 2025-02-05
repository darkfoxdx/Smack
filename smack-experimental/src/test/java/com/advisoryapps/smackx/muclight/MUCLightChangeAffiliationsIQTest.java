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

import com.advisoryapps.smackx.muclight.element.MUCLightChangeAffiliationsIQ;

import org.junit.jupiter.api.Test;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

public class MUCLightChangeAffiliationsIQTest {

    @Test
    public void checkChangeAffiliationsMUCLightStanza() throws Exception {
        HashMap<Jid, MUCLightAffiliation> affiliations = new HashMap<>();
        affiliations.put(JidCreate.from("sarasa2@shakespeare.lit"), MUCLightAffiliation.owner);
        affiliations.put(JidCreate.from("sarasa1@shakespeare.lit"), MUCLightAffiliation.member);
        affiliations.put(JidCreate.from("sarasa3@shakespeare.lit"), MUCLightAffiliation.none);

        MUCLightChangeAffiliationsIQ mucLightChangeAffiliationsIQ = new MUCLightChangeAffiliationsIQ(
                JidCreate.from("coven@muclight.shakespeare.lit"), affiliations);
        mucLightChangeAffiliationsIQ.setStanzaId("member1");

        assertEquals(mucLightChangeAffiliationsIQ.getTo(), "coven@muclight.shakespeare.lit");
        assertEquals(mucLightChangeAffiliationsIQ.getType(), IQ.Type.set);

        HashMap<Jid, MUCLightAffiliation> iqAffiliations = mucLightChangeAffiliationsIQ.getAffiliations();
        assertEquals(iqAffiliations.get(JidCreate.from("sarasa1@shakespeare.lit")), MUCLightAffiliation.member);
        assertEquals(iqAffiliations.get(JidCreate.from("sarasa2@shakespeare.lit")), MUCLightAffiliation.owner);
        assertEquals(iqAffiliations.get(JidCreate.from("sarasa3@shakespeare.lit")), MUCLightAffiliation.none);
    }

}
