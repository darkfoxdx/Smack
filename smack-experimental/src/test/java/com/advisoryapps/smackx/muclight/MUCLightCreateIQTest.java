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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.advisoryapps.smackx.muclight.element.MUCLightCreateIQ;

import org.junit.jupiter.api.Test;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

public class MUCLightCreateIQTest {

    @Test
    public void checkCreateMUCLightStanza() throws Exception {
        List<Jid> occupants = new ArrayList<>();
        occupants.add(JidCreate.from("charlie@test.com"));
        occupants.add(JidCreate.from("pep@test.com"));

        MUCLightCreateIQ mucLightCreateIQ = new MUCLightCreateIQ(
                JidCreate.from("ef498f55-5f79-4238-a5ae-4efe19cbe617@muclight.test.com").asEntityJidIfPossible(),
                "test", occupants);
        mucLightCreateIQ.setStanzaId("1c72W-50");

        assertEquals(mucLightCreateIQ.getConfiguration().getRoomName(), "test");

        HashMap<Jid, MUCLightAffiliation> iqOccupants = mucLightCreateIQ.getOccupants();
        assertEquals(iqOccupants.get(JidCreate.from("charlie@test.com")), MUCLightAffiliation.member);
        assertEquals(iqOccupants.get(JidCreate.from("pep@test.com")), MUCLightAffiliation.member);
    }

}
