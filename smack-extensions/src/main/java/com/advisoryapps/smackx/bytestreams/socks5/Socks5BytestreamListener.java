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
package com.advisoryapps.smackx.bytestreams.socks5;

import com.advisoryapps.smackx.bytestreams.BytestreamListener;
import com.advisoryapps.smackx.bytestreams.BytestreamRequest;

/**
 * Socks5BytestreamListener are informed if a remote user wants to initiate a SOCKS5 Bytestream.
 * Implement this interface to handle incoming SOCKS5 Bytestream requests.
 * <p>
 * There are two ways to add this listener. See
 * {@link Socks5BytestreamManager#addIncomingBytestreamListener(BytestreamListener)} and
 * {@link Socks5BytestreamManager#addIncomingBytestreamListener(BytestreamListener, org.jxmpp.jid.Jid)} for
 * further details.
 *
 * @author Henning Staib
 */
public abstract class Socks5BytestreamListener implements BytestreamListener {

    @Override
    public void incomingBytestreamRequest(BytestreamRequest request) {
        incomingBytestreamRequest((Socks5BytestreamRequest) request);
    }

    /**
     * This listener is notified if a SOCKS5 Bytestream request from another user has been received.
     *
     * @param request the incoming SOCKS5 Bytestream request
     */
    public abstract void incomingBytestreamRequest(Socks5BytestreamRequest request);

}
