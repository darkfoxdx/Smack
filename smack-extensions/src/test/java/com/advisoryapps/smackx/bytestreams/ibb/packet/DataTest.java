/**
 *
 * Copyright the original author or authors
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
package com.advisoryapps.smackx.bytestreams.ibb.packet;

import static com.advisoryapps.smack.test.util.XmlUnitUtils.assertXmlSimilar;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Properties;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.StreamOpen;
import com.advisoryapps.smack.util.stringencoder.Base64;

import com.advisoryapps.smackx.InitExtensions;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Test;
import org.jxmpp.jid.impl.JidCreate;

/**
 * Test for the Data class.
 *
 * @author Henning Staib
 */
public class DataTest extends InitExtensions {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInstantiateWithInvalidArgument() {
        new Data(null);
    }

    @Test
    public void shouldBeOfIQTypeSET() {
        DataPacketExtension dpe = mock(DataPacketExtension.class);
        Data data = new Data(dpe);
        assertEquals(IQ.Type.set, data.getType());
    }

    private static final Properties outputProperties = new Properties();
    {
        outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
    }

    @Test
    public void shouldReturnValidIQStanzaXML() throws Exception {
        String encodedData = Base64.encode("Test");

        String control = XMLBuilder.create("iq")
            .a("from", "romeo@montague.lit/orchard")
            .a("to", "juliet@capulet.lit/balcony")
            .a("id", "kr91n475")
            .a("type", "set")
            .e("data")
                .a("xmlns", "http://jabber.org/protocol/ibb")
                .a("seq", "0")
                .a("sid", "i781hf64")
                .t(encodedData)
            .asString(outputProperties);

        DataPacketExtension dpe = new DataPacketExtension("i781hf64", 0, encodedData);
        Data data = new Data(dpe);
        data.setFrom(JidCreate.from("romeo@montague.lit/orchard"));
        data.setTo(JidCreate.from("juliet@capulet.lit/balcony"));
        data.setStanzaId("kr91n475");

        assertXmlSimilar(control, data.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

}
