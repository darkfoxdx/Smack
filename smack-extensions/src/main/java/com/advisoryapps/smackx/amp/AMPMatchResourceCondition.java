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

public class AMPMatchResourceCondition implements AMPExtension.Condition {

    public static final String NAME = "match-resource";

    /**
     * Check if server supports match-resource condition.
     * @param connection Smack connection instance
     * @return true if match-resource condition is supported.
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
     * Create new amp match-resource condition with value set to one of defined by XEP-0079.
     * See http://xmpp.org/extensions/xep-0079.html#conditions-def-match
     * @param value AMPDeliveryCondition.Value instance that will be used as value parameter. Can't be null.
     */
    public AMPMatchResourceCondition(Value value) {
        if (value == null)
            throw new NullPointerException("Can't create AMPMatchResourceCondition with null value");
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
     * match-resource amp condition value as defined by XEP-0079.
     * See http://xmpp.org/extensions/xep-0079.html#conditions-def-match
     */
    public enum Value {
        /**
         * Destination resource matches any value, effectively ignoring the intended resource.
         * Example: "home/laptop" matches "home", "home/desktop" or "work/desktop"
         */
        any,
        /**
         * Destination resource exactly matches the intended resource.
         * Example: "home/laptop" only matches "home/laptop" and not "home/desktop" or "work/desktop"
         */
        exact,
        /**
         * Destination resource matches any value except for the intended resource.
         * Example: "home/laptop" matches "work/desktop", "home" or "home/desktop", but not "home/laptop"
         */
        other
    }
}
