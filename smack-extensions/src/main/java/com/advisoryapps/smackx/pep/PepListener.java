/**
 *
 * Copyright 2003-2007 Jive Software, 2015 Florian Schmaus.
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

package com.advisoryapps.smackx.pep;

import com.advisoryapps.smack.packet.Message;

import com.advisoryapps.smackx.pubsub.EventElement;

import org.jxmpp.jid.EntityBareJid;


/**
 *
 * A listener that is fired anytime a PEP event message is received.
 *
 * @author Jeff Williams
 * @author Florian Schmaus
 */
public interface PepListener {

    /**
     * Called when PEP events are received as part of a presence subscribe or message filter.
     *
     * @param from the user that sent the entries.
     * @param event the event contained in the message.
     * @param message the message stanza containing the PEP event.
     */
    void eventReceived(EntityBareJid from, EventElement event, Message message);

}
