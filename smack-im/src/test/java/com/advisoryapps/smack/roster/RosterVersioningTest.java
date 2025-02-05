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
package com.advisoryapps.smack.roster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import com.advisoryapps.smack.DummyConnection;
import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.XMPPException;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.IQ.Type;
import com.advisoryapps.smack.packet.Stanza;
import com.advisoryapps.smack.roster.RosterTest.TestRosterListener;
import com.advisoryapps.smack.roster.packet.RosterPacket;
import com.advisoryapps.smack.roster.packet.RosterPacket.Item;
import com.advisoryapps.smack.roster.packet.RosterPacket.ItemType;
import com.advisoryapps.smack.roster.rosterstore.DirectoryRosterStore;
import com.advisoryapps.smack.roster.rosterstore.RosterStore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * Tests that verify the correct behavior of the {@link Roster} implementation
 * with regard to roster versioning.
 *
 * @see Roster
 * @see <a href="http://xmpp.org/rfcs/rfc6121.html#roster">Managing the Roster</a>
 * @author Fabian Schuetz
 * @author Lars Noschinski
 */
public class RosterVersioningTest {

    private DummyConnection connection;
    private Roster roster;
    private TestRosterListener rosterListener;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        DirectoryRosterStore store = DirectoryRosterStore.init(tmpFolder.newFolder("store"));
        populateStore(store);

        connection = new DummyConnection();
        connection.connect();
        connection.login();
        rosterListener = new TestRosterListener();
        roster = Roster.getInstanceFor(connection);
        roster.setRosterStore(store);
        roster.addRosterListener(rosterListener);
        roster.reload();
    }

    @After
    public void tearDown() throws Exception {
        if (connection != null) {
            if (rosterListener != null && roster != null) {
                roster.removeRosterListener(rosterListener);
                rosterListener = null;
            }
            connection.disconnect();
            connection = null;
        }
    }

    /**
     * Tests that receiving an empty roster result causes the roster to be populated
     * by all entries of the roster store.
     * @throws SmackException if Smack detected an exceptional situation.
     * @throws XMPPException if an XMPP protocol error was received.
     */
    @Test(timeout = 300000)
    public void testEqualVersionStored() throws InterruptedException, IOException, XMPPException, SmackException {
        answerWithEmptyRosterResult();
        roster.waitUntilLoaded();

        Collection<RosterEntry> entries = roster.getEntries();
        assertSame("Size of the roster", 3, entries.size());

        HashSet<Item> items = new HashSet<>();
        for (RosterEntry entry : entries) {
            items.add(RosterEntry.toRosterItem(entry));
        }
        RosterStore store = DirectoryRosterStore.init(tmpFolder.newFolder());
        populateStore(store);
        assertEquals("Elements of the roster", new HashSet<>(store.getEntries()), items);

        for (RosterEntry entry : entries) {
            assertTrue("joe stevens".equals(entry.getName()) || "geoff hurley".equals(entry.getName())
                    || "higgins mcmann".equals(entry.getName()));
        }
        Collection<RosterGroup> groups = roster.getGroups();
        assertSame(3, groups.size());

        for (RosterGroup group : groups) {
            assertTrue("all".equals(group.getName()) || "friends".equals(group.getName())
                    || "partners".equals(group.getName()));
        }
    }

    /**
     * Tests that a non-empty roster result empties the store.
     * @throws SmackException if Smack detected an exceptional situation.
     * @throws XMPPException if an XMPP protocol error was received.
     * @throws XmppStringprepException if the provided string is invalid.
     */
    @Test(timeout = 5000)
    public void testOtherVersionStored() throws XMPPException, SmackException, XmppStringprepException {
        Item vaglafItem = vaglafItem();

        // We expect that the roster request is the only packet sent. This is not part of the specification,
        // but a shortcut in the test implementation.
        Stanza sentPacket = connection.getSentPacket();
        if (sentPacket instanceof RosterPacket) {
            RosterPacket sentRP = (RosterPacket) sentPacket;
            RosterPacket answer = new RosterPacket();
            answer.setStanzaId(sentRP.getStanzaId());
            answer.setType(Type.result);
            answer.setTo(sentRP.getFrom());

            answer.setVersion("newVersion");
            answer.addRosterItem(vaglafItem);

            rosterListener.reset();
            connection.processStanza(answer);
            rosterListener.waitUntilInvocationOrTimeout();
        } else {
            assertTrue("Expected to get a RosterPacket ", false);
        }

        Roster roster = Roster.getInstanceFor(connection);
        assertEquals("Size of roster", 1, roster.getEntries().size());
        RosterEntry entry = roster.getEntry(vaglafItem.getJid());
        assertNotNull("Roster contains vaglaf entry", entry);
        assertEquals("vaglaf entry in roster equals the sent entry", vaglafItem, RosterEntry.toRosterItem(entry));

        RosterStore store = roster.getRosterStore();
        assertEquals("Size of store", 1, store.getEntries().size());
        Item item = store.getEntry(vaglafItem.getJid());
        assertNotNull("Store contains vaglaf entry", item);
        assertEquals("vaglaf entry in store equals the sent entry", vaglafItem, item);
    }

    /**
     * Test roster versioning with roster pushes.
     */
    @SuppressWarnings("UndefinedEquals")
    @Test(timeout = 5000)
    public void testRosterVersioningWithCachedRosterAndPushes() throws Throwable {
        answerWithEmptyRosterResult();
        rosterListener.waitAndReset();

        RosterStore store = roster.getRosterStore();

        // Simulate a roster push adding vaglaf
        {
            RosterPacket rosterPush = new RosterPacket();
            rosterPush.setTo(JidCreate.from("rostertest@example.com/home"));
            rosterPush.setType(Type.set);
            rosterPush.setVersion("v97");

            Item pushedItem = vaglafItem();
            rosterPush.addRosterItem(pushedItem);
            rosterListener.reset();
            connection.processStanza(rosterPush);
            rosterListener.waitAndReset();

            assertEquals("Expect store version after push", "v97", store.getRosterVersion());

            Item storedItem = store.getEntry(JidCreate.from("vaglaf@example.com"));
            assertNotNull("Expect vaglaf to be added", storedItem);
            assertEquals("Expect vaglaf to be equal to pushed item", pushedItem, storedItem);

            Collection<Item> rosterItems = new HashSet<>();
            for (RosterEntry entry : roster.getEntries()) {
                rosterItems.add(RosterEntry.toRosterItem(entry));
            }
            assertEquals(rosterItems, new HashSet<>(store.getEntries()));
        }

        // Simulate a roster push removing vaglaf
        {
            RosterPacket rosterPush = new RosterPacket();
            rosterPush.setTo(JidCreate.from("rostertest@example.com/home"));
            rosterPush.setType(Type.set);
            rosterPush.setVersion("v98");

            Item item = new Item(JidCreate.entityBareFrom("vaglaf@example.com"), "vaglaf the only");
            item.setItemType(ItemType.remove);
            rosterPush.addRosterItem(item);
            rosterListener.reset();
            connection.processStanza(rosterPush);
            rosterListener.waitAndReset();

            assertNull("Store doses not contain vaglaf", store.getEntry(JidCreate.entityBareFrom("vaglaf@example.com")));
            assertEquals("Expect store version after push", "v98", store.getRosterVersion());
        }
    }

    private static Item vaglafItem() throws XmppStringprepException {
        Item item = new Item(JidCreate.entityBareFrom("vaglaf@example.com"), "vaglaf the only");
        item.setItemType(ItemType.both);
        item.addGroupName("all");
        item.addGroupName("friends");
        item.addGroupName("partners");
        return item;
    }

    private static void populateStore(RosterStore store) throws IOException {
        store.addEntry(new RosterPacket.Item(JidCreate.entityBareFrom("geoff@example.com"), "geoff hurley"), "");

        RosterPacket.Item item = new RosterPacket.Item(JidCreate.entityBareFrom("joe@example.com"), "joe stevens");
        item.addGroupName("friends");
        item.addGroupName("partners");
        store.addEntry(item, "");

        item = new RosterPacket.Item(JidCreate.entityBareFrom("higgins@example.com"), "higgins mcmann");
        item.addGroupName("all");
        item.addGroupName("friends");
        store.addEntry(item, "v96");
    }

    private void answerWithEmptyRosterResult() {
        // We expect that the roster request is the only packet sent. This is not part of the specification,
        // but a shortcut in the test implementation.
        Stanza sentPacket = connection.getSentPacket();
        if (sentPacket instanceof RosterPacket) {
            final IQ emptyIQ = IQ.createResultIQ((RosterPacket) sentPacket);
            connection.processStanza(emptyIQ);
        } else {
            assertTrue("Expected to get a RosterPacket ", false);
        }
    }

}
