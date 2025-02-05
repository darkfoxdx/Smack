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
package com.advisoryapps.smackx.muc;

import com.advisoryapps.smack.util.Objects;

import com.advisoryapps.smackx.disco.packet.DiscoverItems;

import org.jxmpp.jid.EntityBareJid;

/**
 * Hosted rooms by a chat service may be discovered if they are configured to appear in the room
 * directory . The information that may be discovered is the XMPP address of the room and the room
 * name. The address of the room may be used for obtaining more detailed information
 * {@link com.advisoryapps.smackx.muc.MultiUserChatManager#getRoomInfo(org.jxmpp.jid.EntityBareJid)}
 * or could be used for joining the room
 * {@link com.advisoryapps.smackx.muc.MultiUserChatManager#getMultiUserChat(org.jxmpp.jid.EntityBareJid)}
 * and {@link com.advisoryapps.smackx.muc.MultiUserChat#join(org.jxmpp.jid.parts.Resourcepart)}.
 *
 * @author Gaston Dombiak
 */
public class HostedRoom {

    private final EntityBareJid jid;

    private final String name;

    public HostedRoom(DiscoverItems.Item item) {
        jid = Objects.requireNonNull(item.getEntityID().asEntityBareJidIfPossible(),
                        "The discovered item must be an entity bare JID");
        name = item.getName();
    }

    /**
     * Returns the XMPP address of the hosted room by the chat service. This address may be used
     * when creating a <code>MultiUserChat</code> when joining a room.
     *
     * @return the XMPP address of the hosted room by the chat service.
     */
    public EntityBareJid getJid() {
        return jid;
    }

    /**
     * Returns the name of the room.
     *
     * @return the name of the room.
     */
    public String getName() {
        return name;
    }
}
