/**
 *
 * Copyright 2014 Vyacheslav Blinov
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
package com.advisoryapps.smackx.amp;

import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;

import com.advisoryapps.smackx.amp.packet.AMPExtension;

public class AMPDeliverCondition implements AMPExtension.Condition {

    public static final String NAME = "deliver";

    /**
     * Check if server supports deliver condition.
     * @param connection Smack connection instance
     * @return true if deliver condition is supported.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public static boolean isSupported(XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return AMPManager.isConditionSupported(connection, NAME);
    }

    private final Value value;

    /**
     * Create new amp deliver condition with value set to one of defined by XEP-0079.
     * See http://xmpp.org/extensions/xep-0079.html#conditions-def-deliver
     * @param value AMPDeliveryCondition.Value instance that will be used as value parameter. Can't be null.
     */
    public AMPDeliverCondition(Value value) {
        if (value == null)
            throw new NullPointerException("Can't create AMPDeliverCondition with null value");
        this.value = value;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getValue() {
        return value.toString();
    }

    /**
     * Value for amp deliver condition as defined by XEP-0079.
     * See http://xmpp.org/extensions/xep-0079.html#conditions-def-deliver
     */
    public enum Value {
        /**
         * The message would be immediately delivered to the intended recipient or routed to the next hop.
         */
        direct,
        /**
         * The message would be forwarded to another XMPP address or account.
         */
        forward,
        /**
         * The message would be sent through a gateway to an address or account on a non-XMPP system.
         */
        gateway,
        /**
         * The message would not be delivered at all (e.g., because the intended recipient is offline and message storage is not enabled).
         */
        none,
        /**
         * The message would be stored offline for later delivery to the intended recipient.
         */
        stored
    }
}
