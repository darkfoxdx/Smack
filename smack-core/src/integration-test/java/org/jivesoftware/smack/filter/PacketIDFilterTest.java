/**
 *
 * Copyright 2003 Jive Software.
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

package com.advisoryapps.smack.filter;

import junit.framework.TestCase;
import com.advisoryapps.smack.packet.*;

/**
 * A test case for the PacketIDFilter class.
 */
public class PacketIDFilterTest extends TestCase {

    public void testNullArgs() {
        try {
            new PacketIDFilter(null);
            fail("Parameter can not be null");
        }
        catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    public void testAccept() {
        MockIDPacket packet = new MockIDPacket("foo");

        PacketIDFilter packetIDFilter = new PacketIDFilter("");
        assertFalse(packetIDFilter.accept(packet));

        packetIDFilter = new PacketIDFilter("foo");
        assertTrue(packetIDFilter.accept(packet));

        packetIDFilter = new PacketIDFilter("fOO");
        assertFalse(packetIDFilter.accept(packet));
    }

    /**
     * Wraps the MockPacket class to always give an expected stanza ID field.
     */
    private class MockIDPacket extends MockPacket {
        private String id;
        public MockIDPacket(String id) {
            this.id = id;
        }
        public String getStanzaId() {
            return id;
        }
    }
}
