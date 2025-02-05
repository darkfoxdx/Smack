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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.StreamOpen;
import com.advisoryapps.smack.util.PacketParserUtils;

import com.advisoryapps.smackx.muclight.element.MUCLightConfigurationIQ;
import com.advisoryapps.smackx.muclight.element.MUCLightGetConfigsIQ;

import org.junit.jupiter.api.Test;
import org.jxmpp.jid.impl.JidCreate;

public class MUCLightGetConfigsTest {

    private static final String getConfigsIQExample = "<iq to='coven@muclight.shakespeare.lit' id='config0' type='get'>"
            + "<query xmlns='urn:xmpp:muclight:0#configuration'>" + "<version>abcdefg</version>" + "</query>" + "</iq>";

    private static final String getConfigsResponseExample = "<iq from='coven@muclight.shakespeare.lit' id='getconfig1' "
            + "to='crone1@shakespeare.lit/desktop' type='result'>" + "<query xmlns='urn:xmpp:muclight:0#configuration'>"
            + "<version>123456</version>" + "<roomname>A Dark Cave</roomname>" + "<subject>A subject</subject>"
            + "</query>" + "</iq>";

    private static final String getConfigsResponseExampleWithCustomConfigs = "<iq from='coven@muclight.shakespeare.lit' id='getconfig2' "
            + "to='crone1@shakespeare.lit/desktop' type='result'>" + "<query xmlns='urn:xmpp:muclight:0#configuration'>"
            + "<version>123456</version>" + "<roomname>A Dark Cave</roomname>" + "<color>blue</color>"
            + "<size>20</size>" + "</query>" + "</iq>";

    @Test
    public void checkGetConfigsIQ() throws Exception {
        MUCLightGetConfigsIQ mucLightGetConfigsIQ = new MUCLightGetConfigsIQ(
                JidCreate.from("coven@muclight.shakespeare.lit"), "abcdefg");
        mucLightGetConfigsIQ.setStanzaId("config0");
        assertEquals(getConfigsIQExample, mucLightGetConfigsIQ.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

    @Test
    public void checkGetConfigsResponse() throws Exception {
        IQ iqInfoResult = PacketParserUtils.parseStanza(getConfigsResponseExample);
        MUCLightConfigurationIQ mucLightConfigurationIQ = (MUCLightConfigurationIQ) iqInfoResult;

        assertEquals("123456", mucLightConfigurationIQ.getVersion());
        assertEquals("A Dark Cave", mucLightConfigurationIQ.getConfiguration().getRoomName());
        assertEquals("A subject", mucLightConfigurationIQ.getConfiguration().getSubject());
        assertNull(mucLightConfigurationIQ.getConfiguration().getCustomConfigs());
    }

    @Test
    public void checkGetConfigsResponseWithCustomConfigs() throws Exception {
        IQ iqInfoResult = PacketParserUtils.parseStanza(getConfigsResponseExampleWithCustomConfigs);
        MUCLightConfigurationIQ mucLightConfigurationIQ = (MUCLightConfigurationIQ) iqInfoResult;

        assertEquals("123456", mucLightConfigurationIQ.getVersion());
        assertEquals("A Dark Cave", mucLightConfigurationIQ.getConfiguration().getRoomName());
        assertNull(mucLightConfigurationIQ.getConfiguration().getSubject());

        HashMap<String, String> customConfigs = mucLightConfigurationIQ.getConfiguration().getCustomConfigs();
        assertEquals("blue", customConfigs.get("color"));
        assertEquals("20", customConfigs.get("size"));
    }

}
