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

import com.advisoryapps.smack.packet.StreamOpen;

import com.advisoryapps.smackx.muclight.element.MUCLightDestroyIQ;

import org.junit.jupiter.api.Test;
import org.jxmpp.jid.impl.JidCreate;

public class MUCLightDestroyTest {

    private static final String stanza = "<iq to='coven@muclight.shakespeare.lit' id='destroy1' type='set'>"
            + "<query xmlns='urn:xmpp:muclight:0#destroy'/>" + "</iq>";

    @Test
    public void checkDestroyMUCLightStanza() throws Exception {
        MUCLightDestroyIQ mucLightDestroyIQ = new MUCLightDestroyIQ(JidCreate.from("coven@muclight.shakespeare.lit"));
        mucLightDestroyIQ.setStanzaId("destroy1");
        assertEquals(mucLightDestroyIQ.toXML(StreamOpen.CLIENT_NAMESPACE).toString(), stanza);
    }

}
