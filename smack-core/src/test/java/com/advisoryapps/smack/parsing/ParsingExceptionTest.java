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
package com.advisoryapps.smack.parsing;

import static com.advisoryapps.smack.test.util.CharSequenceEquals.equalsCharSequence;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.provider.ProviderManager;
import com.advisoryapps.smack.test.util.TestUtils;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParsingExceptionTest {

    private static final String EXTENSION2 =
    "<extension2 xmlns='namespace'>" +
        "<bar node='testNode'>" +
            "<i id='testid1'>" +
              "text content" +
            "</i>" +
        "</bar>" +
     "</extension2>";

    @Before
    public void init() {
        ProviderManager.addExtensionProvider(ThrowException.ELEMENT, ThrowException.NAMESPACE, new ThrowException());
    }

    @After
    public void tini() {
        ProviderManager.removeExtensionProvider(ThrowException.ELEMENT, ThrowException.NAMESPACE);
    }

    @Test
    public void consumeUnparsedInput() throws Exception {
        final String MESSAGE_EXCEPTION_ELEMENT =
                        "<" + ThrowException.ELEMENT + " xmlns='" + ThrowException.NAMESPACE + "'>" +
                            "<nothingInHere/>" +
                        "</" + ThrowException.ELEMENT + ">";
        XmlPullParser parser = TestUtils.getMessageParser(
                "<message from='user@server.example' to='francisco@denmark.lit' id='foo'>" +
                    MESSAGE_EXCEPTION_ELEMENT +
                    EXTENSION2 +
                "</message>");
        int parserDepth = parser.getDepth();
        CharSequence content = null;
        try {
            PacketParserUtils.parseMessage(parser);
        } catch (Exception e) {
            content = PacketParserUtils.parseContentDepth(parser, parserDepth, false);
        }
        assertThat(MESSAGE_EXCEPTION_ELEMENT + EXTENSION2 + "</message>", equalsCharSequence(content));
    }

    static class ThrowException extends ExtensionElementProvider<ExtensionElement> {
        public static final String ELEMENT = "exception";
        public static final String NAMESPACE = "http://smack.jivesoftware.org/exception";

        @Override
        public ExtensionElement parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws IOException {
            throw new IOException("Test Exception");
        }

    }
}
