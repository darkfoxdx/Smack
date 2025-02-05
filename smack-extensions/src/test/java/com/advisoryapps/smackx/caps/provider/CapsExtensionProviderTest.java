/**
 *
 * Copyright 2015-2019 Florian Schmaus
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
package com.advisoryapps.smackx.caps.provider;

import static org.junit.Assert.assertNotNull;

import com.advisoryapps.smack.test.util.SmackTestUtil;

import com.advisoryapps.smackx.InitExtensions;
import com.advisoryapps.smackx.caps.packet.CapsExtension;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class CapsExtensionProviderTest extends InitExtensions {

    @ParameterizedTest
    @EnumSource(SmackTestUtil.XmlPullParserKind.class)
    public void parseTest(SmackTestUtil.XmlPullParserKind parserKind) throws Exception {
        // @formatter:off
        final String capsExtensionString =
            "<c xmlns='http://jabber.org/protocol/caps'"
            + " hash='sha-1'"
            + " node='http://foo.example.org/bar'"
            + " ver='QgayPKawpkPSDYmwt/WM94uA1u0='/>";
        // @formatter:on
        CapsExtension capsExtension = SmackTestUtil.parse(capsExtensionString, CapsExtensionProvider.class, parserKind);
        assertNotNull(capsExtension);
    }
}
