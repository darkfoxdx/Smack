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

import java.util.Properties;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.StreamOpen;

import com.advisoryapps.smackx.InitExtensions;

import com.jamesmurty.utils.XMLBuilder;
import org.junit.Test;
import org.jxmpp.jid.impl.JidCreate;

/**
 * Test for the Close class.
 *
 * @author Henning Staib
 */
public class CloseTest extends InitExtensions {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInstantiateWithInvalidArguments1() {
        new Close(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInstantiateWithInvalidArguments2() {
        new Close("");
    }

    @Test
    public void shouldBeOfIQTypeSET() {
        Close close = new Close("sessionID");
        assertEquals(IQ.Type.set, close.getType());
    }

    @Test
    public void shouldSetAllFieldsCorrectly() {
        Close close = new Close("sessionID");
        assertEquals("sessionID", close.getSessionID());
    }

    private static final Properties outputProperties = new Properties();
    {
        outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
    }

    @Test
    public void shouldReturnValidIQStanzaXML() throws Exception {
        String control = XMLBuilder.create("iq")
            .a("to", "juliet@capulet.lit/balcony")
            .a("from", "romeo@montague.lit/orchard")
            .a("id", "us71g45j")
            .a("type", "set")
            .e("close")
                .a("xmlns", "http://jabber.org/protocol/ibb")
                .a("sid", "i781hf64")
            .asString(outputProperties);

        Close close = new Close("i781hf64");
        close.setFrom(JidCreate.from("romeo@montague.lit/orchard"));
        close.setTo(JidCreate.from("juliet@capulet.lit/balcony"));
        close.setStanzaId("us71g45j");

        assertXmlSimilar(control, close.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
    }

}
