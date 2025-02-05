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

import com.advisoryapps.smack.packet.Message;

import com.advisoryapps.smackx.muc.packet.MUCUser;

import org.jxmpp.jid.EntityBareJid;

/**
 * A listener that is fired anytime an invitee declines or rejects an invitation.
 *
 * @author Gaston Dombiak
 */
public interface InvitationRejectionListener {

    /**
     * Called when the invitee declines the invitation.
     *
     * @param invitee the invitee that declined the invitation. (e.g. hecate@shakespeare.lit).
     * @param reason the reason why the invitee declined the invitation.
     * @param message the message used to decline the invitation.
     * @param rejection the raw decline found in the message.
     */
    void invitationDeclined(EntityBareJid invitee, String reason, Message message, MUCUser.Decline rejection);

}
