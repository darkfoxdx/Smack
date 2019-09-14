/**
 *
 * Copyright © 2014 Florian Schmaus
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
package com.advisoryapps.smack;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.TestIQ;
import com.advisoryapps.smack.util.StringUtils;

import org.junit.Test;

public class StanzaIdTest {

    @Test
    public void testIqId() {
        IQ iq1 = new TestIQ();
        String iq1Id = iq1.getStanzaId();
        assertTrue(StringUtils.isNotEmpty(iq1Id));

        IQ iq2 = new TestIQ();
        String iq2Id = iq2.getStanzaId();
        assertTrue(StringUtils.isNotEmpty(iq2Id));

        assertFalse(iq1Id.equals(iq2Id));
    }

}
