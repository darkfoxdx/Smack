/**
 *
 * Copyright 2003-2006 Jive Software.
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

package com.advisoryapps.smackx.jingleold.listeners;

import com.advisoryapps.smack.SmackException.NotConnectedException;

import com.advisoryapps.smackx.jingleold.media.PayloadType;

/**
 * Interface for listening to jmf events.
 * @author Thiago Camargo
 */
public interface JingleMediaListener extends JingleListener {
    /**
     * Notification that the jmf has been negotiated and established.
     *
     * @param pt The payload type agreed.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    void mediaEstablished(PayloadType pt) throws NotConnectedException, InterruptedException;

    /**
     * Notification that a payload type must be cancelled.
     *
     * @param cand The payload type that must be closed
     */
    void mediaClosed(PayloadType cand);
}
