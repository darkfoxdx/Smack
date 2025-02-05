/**
 *
 * Copyright 2003-2005 Jive Software.
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

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;

import com.advisoryapps.smackx.jingleold.JingleSession;

/**
 * Bridged Resolver use a RTPBridge Service to add a relayed candidate.
 * A very reliable solution for NAT Traversal.
 *
 * The resolver verify is the XMPP Server that the client is connected offer this service.
 * If the server supports, a candidate is requested from the service.
 * The resolver adds this candidate
 */
public class BridgedResolver extends TransportResolver {
    private static final Logger LOGGER = Logger.getLogger(BridgedResolver.class.getName());

    private final XMPPConnection connection;

    private final Random random = new Random();

    private long sid;

    /**
     * Constructor.
     * A Bridged Resolver need an XMPPConnection to connect to a RTP Bridge.
     *
     * @param connection the XMPP connection.
     */
    public BridgedResolver(XMPPConnection connection) {
        super();
        this.connection = connection;
    }

    /**
     * Resolve Bridged Candidate.
     *
     * The BridgedResolver takes the IP address and ports of a jmf proxy service.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    @Override
    public synchronized void resolve(JingleSession session) throws XMPPException, NotConnectedException, InterruptedException {

        setResolveInit();

        clearCandidates();

        sid = random.nextInt(Integer.MAX_VALUE);

        RTPBridge rtpBridge = RTPBridge.getRTPBridge(connection, String.valueOf(sid));

        String localIp = getLocalHost();

        TransportCandidate localCandidate = new TransportCandidate.Fixed(
                rtpBridge.getIp(), rtpBridge.getPortA());
        localCandidate.setLocalIp(localIp);

        TransportCandidate remoteCandidate = new TransportCandidate.Fixed(
                rtpBridge.getIp(), rtpBridge.getPortB());
        remoteCandidate.setLocalIp(localIp);

        localCandidate.setSymmetric(remoteCandidate);
        remoteCandidate.setSymmetric(localCandidate);

        localCandidate.setPassword(rtpBridge.getPass());
        remoteCandidate.setPassword(rtpBridge.getPass());

        localCandidate.setSessionId(rtpBridge.getSid());
        remoteCandidate.setSessionId(rtpBridge.getSid());

        localCandidate.setConnection(this.connection);
        remoteCandidate.setConnection(this.connection);

        addCandidate(localCandidate);

        setResolveEnd();
    }

    @Override
    public void initialize() throws SmackException.SmackMessageException, XMPPErrorException, InterruptedException,
                    NoResponseException, NotConnectedException {

        clearCandidates();

        if (!RTPBridge.serviceAvailable(connection)) {
            setInitialized();
            throw new SmackException.SmackMessageException("No RTP Bridge service available");
        }
        setInitialized();

    }

    @Override
    public void cancel() throws XMPPException {
        // Nothing to do here
    }

    public static String getLocalHost() {
        Enumeration<NetworkInterface> ifaces = null;

        try {
            ifaces = NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException e) {
            LOGGER.log(Level.WARNING, "exception", e);
        }

        while (ifaces.hasMoreElements()) {

            NetworkInterface iface = ifaces.nextElement();
            Enumeration<InetAddress> iaddresses = iface.getInetAddresses();

            while (iaddresses.hasMoreElements()) {
                InetAddress iaddress = iaddresses.nextElement();
                if (!iaddress.isLoopbackAddress() && !iaddress.isLinkLocalAddress() && !iaddress.isSiteLocalAddress() && !(iaddress instanceof Inet6Address)) {
                    return iaddress.getHostAddress();
                }
            }
        }

        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "exception", e);
        }

        return "127.0.0.1";

    }

}
