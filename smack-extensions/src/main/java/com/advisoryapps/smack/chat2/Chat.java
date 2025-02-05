/**
 *
 * Copyright 2017 Florian Schmaus.
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
package com.advisoryapps.smack.chat2;

import com.advisoryapps.smack.Manager;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.packet.Presence;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;

public final class Chat extends Manager {

    private final EntityBareJid jid;

    volatile EntityFullJid lockedResource;

    Presence lastPresenceOfLockedResource;

    Chat(final XMPPConnection connection, EntityBareJid jid) {
        super(connection);
        this.jid = jid;
    }

    public void send(CharSequence message) throws NotConnectedException, InterruptedException {
        Message stanza = new Message();
        stanza.setBody(message);
        stanza.setType(Message.Type.chat);
        send(stanza);
    }

    public void send(Message message) throws NotConnectedException, InterruptedException {
        switch (message.getType()) {
        case normal:
        case chat:
            break;
        default:
            throw new IllegalArgumentException("Message must be of type 'normal' or 'chat'");
        }

        Jid to = lockedResource;
        if (to == null) {
            to = jid;
        }
        message.setTo(to);

        connection().sendStanza(message);
    }

    public EntityBareJid getXmppAddressOfChatPartner() {
        return jid;
    }

    void unlockResource() {
        lockedResource = null;
        lastPresenceOfLockedResource = null;
    }
}
