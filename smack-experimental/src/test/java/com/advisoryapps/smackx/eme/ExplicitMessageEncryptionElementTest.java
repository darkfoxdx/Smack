/**
 *
 * Copyright 2018 Paul Schaub
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
package com.advisoryapps.smackx.eme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.test.util.SmackTestSuite;

import com.advisoryapps.smackx.eme.element.ExplicitMessageEncryptionElement;

import org.junit.jupiter.api.Test;

public class ExplicitMessageEncryptionElementTest extends SmackTestSuite {

    @Test
    public void addToMessageTest() {
        Message message = new Message();

        // Check inital state (no elements)
        assertNull(ExplicitMessageEncryptionElement.from(message));
        assertFalse(ExplicitMessageEncryptionElement.hasProtocol(message,
                ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.omemoVAxolotl));

        List<ExtensionElement> extensions = message.getExtensions();
        assertEquals(0, extensions.size());

        // Add OMEMO
        ExplicitMessageEncryptionElement.set(message,
                ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.omemoVAxolotl);
        extensions = message.getExtensions();
        assertEquals(1, extensions.size());
        assertTrue(ExplicitMessageEncryptionElement.hasProtocol(message,
                ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.omemoVAxolotl));
        assertTrue(ExplicitMessageEncryptionElement.hasProtocol(message,
                ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.omemoVAxolotl.getNamespace()));
        assertFalse(ExplicitMessageEncryptionElement.hasProtocol(message,
                ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.openpgpV0));
        assertFalse(ExplicitMessageEncryptionElement.hasProtocol(message,
                ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.openpgpV0.getNamespace()));

        ExplicitMessageEncryptionElement.set(message,
                ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.openpgpV0);
        extensions = message.getExtensions();
        assertEquals(2, extensions.size());
        assertTrue(ExplicitMessageEncryptionElement.hasProtocol(message,
                ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.openpgpV0));
        assertTrue(ExplicitMessageEncryptionElement.hasProtocol(message,
                ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.omemoVAxolotl));

        // Check, if adding additional OMEMO wont add another element
        ExplicitMessageEncryptionElement.set(message,
                ExplicitMessageEncryptionElement.ExplicitMessageEncryptionProtocol.omemoVAxolotl);

        extensions = message.getExtensions();
        assertEquals(2, extensions.size());
    }
}
