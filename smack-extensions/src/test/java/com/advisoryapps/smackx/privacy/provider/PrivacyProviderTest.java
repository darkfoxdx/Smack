/**
 *
 * Copyright 2012-2019 Florian Schmaus
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
package com.advisoryapps.smackx.privacy.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.util.PacketParserUtils;

import com.advisoryapps.smackx.InitExtensions;
import com.advisoryapps.smackx.privacy.packet.Privacy;
import com.advisoryapps.smackx.privacy.packet.PrivacyItem;

import org.junit.Test;

public class PrivacyProviderTest extends InitExtensions {

    @Test
    public void parsePrivacyList() throws Exception {
        // @formatter:off
        final String xmlPrivacyList =
        "<iq type='result' id='getlist2' to='romeo@example.net/orchard'>"
          + "<query xmlns='jabber:iq:privacy'>"
          + "<list name='public'>"
          + "<item type='jid' "
          + "value='tybalt@example.com' "
          + "action='deny' "
          + "order='1'/>"
          + "<item action='allow' order='2'/>"
          + "</list>"
          + "</query>"
          + "</iq>";
        // @formatter:on
        IQ iqPrivacyList = PacketParserUtils.parseStanza(xmlPrivacyList);
        assertTrue(iqPrivacyList instanceof Privacy);

        Privacy privacyList = (Privacy) iqPrivacyList;
        List<PrivacyItem> pl = privacyList.getPrivacyList("public");

        PrivacyItem first = pl.get(0);
        assertEquals(PrivacyItem.Type.jid, first.getType());
        assertEquals("tybalt@example.com", first.getValue());
        assertEquals(false, first.isAllow());
        assertEquals(1L, first.getOrder().nativeRepresentation());

        PrivacyItem second = pl.get(1);
        assertEquals(true, second.isAllow());
        assertEquals(2L, second.getOrder().nativeRepresentation());
    }

    @Test
    public void parsePrivacyListWithFallThroughInclChildElements() throws Exception {
        // @formatter:off
        final String xmlPrivacyList =
        "<iq type='result' id='getlist2' to='romeo@example.net/orchard'>"
          + "<query xmlns='jabber:iq:privacy'>"
          + "<list name='public'>"
          + "<item type='jid' "
          + "value='tybalt@example.com' "
          + "action='deny' "
          + "order='1'/>"
          + "<item action='allow' order='2'>"
            + "<message/>"
            + "<presence-in/>"
          + "</item>"
          + "</list>"
          + "</query>"
          + "</iq>";
        // @formatter:on
        IQ iqPrivacyList = PacketParserUtils.parseStanza(xmlPrivacyList);
        assertTrue(iqPrivacyList instanceof Privacy);

        Privacy privacyList = (Privacy) iqPrivacyList;
        List<PrivacyItem> pl = privacyList.getPrivacyList("public");

        PrivacyItem first = pl.get(0);
        assertEquals(PrivacyItem.Type.jid, first.getType());
        assertEquals("tybalt@example.com", first.getValue());
        assertEquals(false, first.isAllow());
        assertEquals(1L, first.getOrder().nativeRepresentation());

        PrivacyItem second = pl.get(1);
        assertTrue(second.isAllow());
        assertEquals(2L, second.getOrder().nativeRepresentation());
        assertTrue(second.isFilterMessage());
        assertTrue(second.isFilterPresenceIn());
        assertFalse(second.isFilterPresenceOut());
        assertFalse(second.isFilterIQ());
    }
}
