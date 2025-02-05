/**
 *
 * Copyright 2003-2007 Jive Software.
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

package com.advisoryapps.smackx;

import com.advisoryapps.smack.*;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.test.SmackTestCase;
import com.advisoryapps.smack.filter.PacketFilter;
import com.advisoryapps.smack.filter.StanzaExtensionFilter;

/**
 *
 *
 * @author Matt Tucker
 */
public class GroupChatInvitationTest extends SmackTestCase {

    private StanzaCollector collector = null;

    /**
     * Constructor for GroupChatInvitationTest.
     * @param arg0 TODO javadoc me please
     */
    public GroupChatInvitationTest(String arg0) {
        super(arg0);
    }

    public void testInvitation() {
        try {
            GroupChatInvitation invitation = new GroupChatInvitation("test@" + getChatDomain());
            Message message = new Message(getBareJID(1));
            message.setBody("Group chat invitation!");
            message.addExtension(invitation);
            getConnection(0).sendStanza(message);

            Thread.sleep(250);

            Message result = (Message)collector.pollResult();
            assertNotNull("Message not delivered correctly.", result);

            GroupChatInvitation resultInvite = (GroupChatInvitation)result.getExtension("x",
                    "jabber:x:conference");

            assertEquals("Invitation not to correct room", "test@" + getChatDomain(),
                    resultInvite.getRoomAddress());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        // Register listener for groupchat invitations.
        PacketFilter filter = new StanzaExtensionFilter("x", "jabber:x:conference");
        collector = getConnection(1).createStanzaCollector(filter);
    }

    protected void tearDown() throws Exception {
        // Cancel the packet collector so that no more results are queued up
        collector.cancel();

        super.tearDown();
    }

    protected int getMaxConnections() {
        return 2;
    }
}
