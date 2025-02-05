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
package com.advisoryapps.smackx.jingleold.nat;

import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPException;

import com.advisoryapps.smackx.jingleold.JingleSession;

/**
 * The FixedResolver is a resolver where
 * the external address and port are previously known when the object is
 * initialized.
 *
 * @author Alvaro Saurin
 */
public class FixedResolver extends TransportResolver {

    TransportCandidate fixedCandidate;

    /**
     * Constructor.
     *
     * @param ip the IP address.
     * @param port the port number.
     */
    public FixedResolver(String ip, int port) {
        super();
        setFixedCandidate(ip, port);
    }

    /**
     * Create a basic resolver, where we provide the IP and port.
     *
     * @param ip   an IP address
     * @param port a port
     */
    public void setFixedCandidate(String ip, int port) {
        fixedCandidate = new TransportCandidate.Fixed(ip, port);
    }

    /**
     * Resolve the IP address.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    @Override
    public synchronized void resolve(JingleSession session) throws XMPPException, NotConnectedException, InterruptedException {
        if (!isResolving()) {
            setResolveInit();

            clearCandidates();

            if (fixedCandidate.getLocalIp() == null)
                fixedCandidate.setLocalIp(fixedCandidate.getIp());

            if (fixedCandidate != null) {
                addCandidate(fixedCandidate);
            }

            setResolveEnd();
        }
    }

    /**
     * Initialize the resolver.
     *
     * @throws XMPPException if an XMPP protocol error was received.
     */
    @Override
    public void initialize() throws XMPPException {
        setInitialized();
    }

    @Override
    public void cancel() throws XMPPException {
        // Nothing to do here
    }
}
