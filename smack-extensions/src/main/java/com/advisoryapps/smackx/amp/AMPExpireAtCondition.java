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

import java.util.Date;

import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;

import com.advisoryapps.smackx.amp.packet.AMPExtension;

import org.jxmpp.util.XmppDateTime;


public class AMPExpireAtCondition implements AMPExtension.Condition {

    public static final String NAME = "expire-at";

    /**
     * Check if server supports expire-at condition.
     * @param connection Smack connection instance
     * @return true if expire-at condition is supported.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public static boolean isSupported(XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException  {
        return AMPManager.isConditionSupported(connection, NAME);
    }

    private final String value;

    /**
     * Create new expire-at amp condition with value set as XEP-0082 formatted date.
     * @param utcDateTime Date instance of time
     *                    that will be used as value parameter after formatting to XEP-0082 format. Can't be null.
     */
    public AMPExpireAtCondition(Date utcDateTime) {
        if (utcDateTime == null)
            throw new NullPointerException("Can't create AMPExpireAtCondition with null value");
        this.value = XmppDateTime.formatXEP0082Date(utcDateTime);
    }

    /**
     * Create new expire-at amp condition with defined value.
     * @param utcDateTime UTC time string that will be used as value parameter.
     *                    Should be formatted as XEP-0082 Date format. Can't be null.
     */
    public AMPExpireAtCondition(String utcDateTime) {
        if (utcDateTime == null)
            throw new NullPointerException("Can't create AMPExpireAtCondition with null value");
        this.value = utcDateTime;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getValue() {
        return value;
    }

}
