/**
 *
 * Copyright © 2017 Grigory Fedorov
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
package com.advisoryapps.smackx.httpfileupload.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.util.PacketParserUtils;

import com.advisoryapps.smackx.httpfileupload.element.FileTooLargeError;

import org.junit.jupiter.api.Test;

public class FileTooLargeErrorProviderTest {

    /**
     * Example 7. Alternative response by the upload service if the file size was too large
     * @see <a href="http://xmpp.org/extensions/xep-0363.html#errors">XEP-0363: HTTP File Upload 5. Error conditions</a>
     */
    private static final String slotErrorFileToLarge
            = "<iq from='upload.montague.tld' "
            +       "id='step_03' "
            +       "to='romeo@montague.tld/garden' "
            +       "type='error'>"
            +   "<request xmlns='urn:xmpp:http:upload:0'>"
            +       "<filename>my_juliet.png</filename>"
            +       "<size>23456</size>"
            +   "</request>"
            +   "<error type='modify'>"
            +       "<not-acceptable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas' />"
            +       "<text xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>File too large. The maximum file size is 20000 bytes</text>"
            +       "<file-too-large xmlns='urn:xmpp:http:upload:0'>"
            +           "<max-file-size>20000</max-file-size>"
            +       "</file-too-large>"
            +   "</error>"
            + "</iq>";

    @Test
    public void checkSlotErrorFileToLarge() throws Exception {
        IQ fileTooLargeErrorIQ = PacketParserUtils.parseStanza(slotErrorFileToLarge);

        assertEquals(IQ.Type.error, fileTooLargeErrorIQ.getType());

        FileTooLargeError fileTooLargeError = FileTooLargeError.from(fileTooLargeErrorIQ);
        assertEquals(20000, fileTooLargeError.getMaxFileSize());
    }
}
