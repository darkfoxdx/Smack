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
package com.advisoryapps.smackx.omemo;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import com.advisoryapps.smack.test.util.SmackTestSuite;
import com.advisoryapps.smack.test.util.TestUtils;
import com.advisoryapps.smack.util.stringencoder.Base64;
import com.advisoryapps.smack.xml.XmlPullParser;

import com.advisoryapps.smackx.omemo.element.OmemoBundleElement_VAxolotl;
import com.advisoryapps.smackx.omemo.provider.OmemoBundleVAxolotlProvider;

import org.junit.Test;

/**
 * Test serialization and parsing of the OmemoBundleVAxolotlElement.
 */
public class OmemoBundleVAxolotlElementTest extends SmackTestSuite {

    @Test
    public void serializationTest() throws Exception {
        int signedPreKeyId = 420;
        String signedPreKeyB64 = Base64.encodeToString("SignedPreKey".getBytes(StandardCharsets.UTF_8));
        String signedPreKeySigB64 = Base64.encodeToString("SignedPreKeySignature".getBytes(StandardCharsets.UTF_8));
        String identityKeyB64 = Base64.encodeToString("IdentityKey".getBytes(StandardCharsets.UTF_8));
        int preKeyId1 = 220, preKeyId2 = 284;
        String preKey1B64 = Base64.encodeToString("FirstPreKey".getBytes(StandardCharsets.UTF_8)),
                preKey2B64 = Base64.encodeToString("SecondPreKey".getBytes(StandardCharsets.UTF_8));
        HashMap<Integer, String> preKeysB64 = new HashMap<>();
        preKeysB64.put(preKeyId1, preKey1B64);
        preKeysB64.put(preKeyId2, preKey2B64);

        OmemoBundleElement_VAxolotl bundle = new OmemoBundleElement_VAxolotl(signedPreKeyId,
                signedPreKeyB64, signedPreKeySigB64, identityKeyB64, preKeysB64);

        assertEquals("ElementName must match.", "bundle", bundle.getElementName());
        assertEquals("Namespace must match.", "eu.siacs.conversations.axolotl", bundle.getNamespace());

        String expected =
                "<bundle xmlns='eu.siacs.conversations.axolotl'>" +
                    "<signedPreKeyPublic signedPreKeyId='420'>" +
                        signedPreKeyB64 +
                    "</signedPreKeyPublic>" +
                    "<signedPreKeySignature>" +
                        signedPreKeySigB64 +
                    "</signedPreKeySignature>" +
                    "<identityKey>" +
                        identityKeyB64 +
                    "</identityKey>" +
                    "<prekeys>" +
                        "<preKeyPublic preKeyId='220'>" +
                            preKey1B64 +
                        "</preKeyPublic>" +
                        "<preKeyPublic preKeyId='284'>" +
                            preKey2B64 +
                        "</preKeyPublic>" +
                    "</prekeys>" +
                "</bundle>";
        String actual = bundle.toXML().toString();
        assertEquals("Bundles XML must match.", expected, actual);

        byte[] signedPreKey = "SignedPreKey".getBytes(StandardCharsets.UTF_8);
        byte[] signedPreKeySig = "SignedPreKeySignature".getBytes(StandardCharsets.UTF_8);
        byte[] identityKey = "IdentityKey".getBytes(StandardCharsets.UTF_8);
        byte[] firstPreKey = "FirstPreKey".getBytes(StandardCharsets.UTF_8);
        byte[] secondPreKey = "SecondPreKey".getBytes(StandardCharsets.UTF_8);

        OmemoBundleElement_VAxolotl parsed = new OmemoBundleVAxolotlProvider().parse(TestUtils.getParser(actual));

        assertTrue("B64-decoded signedPreKey must match.", Arrays.equals(signedPreKey, parsed.getSignedPreKey()));
        assertEquals("SignedPreKeyId must match", signedPreKeyId, parsed.getSignedPreKeyId());
        assertTrue("B64-decoded signedPreKey signature must match.", Arrays.equals(signedPreKeySig, parsed.getSignedPreKeySignature()));
        assertTrue("B64-decoded identityKey must match.", Arrays.equals(identityKey, parsed.getIdentityKey()));
        assertTrue("B64-decoded first preKey must match.", Arrays.equals(firstPreKey, parsed.getPreKey(220)));
        assertTrue("B64-decoded second preKey must match.", Arrays.equals(secondPreKey, parsed.getPreKey(284)));
        assertEquals("toString outputs must match.", bundle.toString(), parsed.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyPreKeysShouldFailTest() throws Exception {
        String s = "<bundle xmlns='eu.siacs.conversations.axolotl'><signedPreKeyPublic signedPreKeyId='1'>BU4bJ18+rqbSnBblZU8pR/s+impyhoL9AJssJIE59fZb</signedPreKeyPublic><signedPreKeySignature>MaQtv7ySqHpPr0gkVtMp4KmWC61Hnfs5a7/cKEhrX8n12evGdkg4fNf3Q/ufgmJu5dnup9pkTA1pj00dTbtXjw==</signedPreKeySignature><identityKey>BWO0QOem1YXIJuT61cxXpG/mKlvISDwZxQJHW2/7eVki</identityKey><prekeys></prekeys></bundle>";
        XmlPullParser parser = TestUtils.getParser(s);
        new OmemoBundleVAxolotlProvider().parse(parser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingPreKeysShouldAlsoFailTest() throws Exception {
        String s = "<bundle xmlns='eu.siacs.conversations.axolotl'><signedPreKeyPublic signedPreKeyId='1'>BU4bJ18+rqbSnBblZU8pR/s+impyhoL9AJssJIE59fZb</signedPreKeyPublic><signedPreKeySignature>MaQtv7ySqHpPr0gkVtMp4KmWC61Hnfs5a7/cKEhrX8n12evGdkg4fNf3Q/ufgmJu5dnup9pkTA1pj00dTbtXjw==</signedPreKeySignature><identityKey>BWO0QOem1YXIJuT61cxXpG/mKlvISDwZxQJHW2/7eVki</identityKey></bundle>";
        XmlPullParser parser = TestUtils.getParser(s);
        new OmemoBundleVAxolotlProvider().parse(parser);
    }
}
