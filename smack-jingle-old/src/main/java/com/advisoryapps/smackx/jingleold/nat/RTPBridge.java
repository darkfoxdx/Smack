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

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.StanzaCollector;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.provider.ProviderManager;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;
import com.advisoryapps.smackx.disco.packet.DiscoverInfo;

/**
 * RTPBridge IQ Stanza used to request and retrieve a RTPBridge Candidates that can be used for a Jingle Media Transmission between two parties that are behind NAT.
 * This Jingle Bridge has all the needed information to establish a full UDP Channel (Send and Receive) between two parties.
 * <i>This transport method should be used only if other transport methods are not allowed. Or if you want a more reliable transport.</i>
 *
 * High Level Usage Example:
 *
 * RTPBridge rtpBridge = RTPBridge.getRTPBridge(connection, sessionID);
 *
 * @author Thiago Camargo
 */
public class RTPBridge extends IQ {

    private static final Logger LOGGER = Logger.getLogger(RTPBridge.class.getName());

    private String sid;
    private String pass;
    private String ip;
    private String name;
    private int portA = -1;
    private int portB = -1;
    private String hostA;
    private String hostB;
    private BridgeAction bridgeAction = BridgeAction.create;

    private enum BridgeAction {

        create, change, publicip
    }

    /**
     * Element name of the stanza extension.
     */
    public static final String NAME = "rtpbridge";

    /**
     * Element name of the stanza extension.
     */
    public static final String ELEMENT_NAME = "rtpbridge";

    /**
     * Namespace of the stanza extension.
     */
    public static final String NAMESPACE = "http://www.jivesoftware.com/protocol/rtpbridge";

    static {
        ProviderManager.addIQProvider(NAME, NAMESPACE, new Provider());
    }

    /**
     * Creates a RTPBridge Instance with defined Session ID.
     *
     * @param sid TODO javadoc me please
     */
    public RTPBridge(String sid) {
        this();
        this.sid = sid;
    }

    /**
     * Creates a RTPBridge Instance with defined Session ID.
     *
     * @param action TODO javadoc me please
     */
    public RTPBridge(BridgeAction action) {
        this();
        this.bridgeAction = action;
    }

    /**
     * Creates a RTPBridge Instance with defined Session ID.
     *
     * @param sid TODO javadoc me please
     * @param bridgeAction TODO javadoc me please
     */
    public RTPBridge(String sid, BridgeAction bridgeAction) {
        this();
        this.sid = sid;
        this.bridgeAction = bridgeAction;
    }

    /**
     * Creates a RTPBridge Stanza without Session ID.
     */
    public RTPBridge() {
        super(ELEMENT_NAME, NAMESPACE);
    }

    /**
     * Get the attributes string.
     *
     * @return the attributes.
     */
    public String getAttributes() {
        StringBuilder str = new StringBuilder();

        if (getSid() != null)
            str.append(" sid='").append(getSid()).append('\'');

        if (getPass() != null)
            str.append(" pass='").append(getPass()).append('\'');

        if (getPortA() != -1)
            str.append(" porta='").append(getPortA()).append('\'');

        if (getPortB() != -1)
            str.append(" portb='").append(getPortB()).append('\'');

        if (getHostA() != null)
            str.append(" hosta='").append(getHostA()).append('\'');

        if (getHostB() != null)
            str.append(" hostb='").append(getHostB()).append('\'');

        return str.toString();
    }

    /**
     * Get the Session ID of the Stanza (usually same as Jingle Session ID).
     *
     * @return the session ID
     */
    public String getSid() {
        return sid;
    }

    /**
     * Set the Session ID of the Stanza (usually same as Jingle Session ID).
     *
     * @param sid TODO javadoc me please
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * Get the Host A IP Address.
     *
     * @return the Host A IP Address
     */
    public String getHostA() {
        return hostA;
    }

    /**
     * Set the Host A IP Address.
     *
     * @param hostA TODO javadoc me please
     */
    public void setHostA(String hostA) {
        this.hostA = hostA;
    }

    /**
     * Get the Host B IP Address.
     *
     * @return the Host B IP Address
     */
    public String getHostB() {
        return hostB;
    }

    /**
     * Set the Host B IP Address.
     *
     * @param hostB TODO javadoc me please
     */
    public void setHostB(String hostB) {
        this.hostB = hostB;
    }

    /**
     * Get Side A receive port.
     *
     * @return the side A receive port
     */
    public int getPortA() {
        return portA;
    }

    /**
     * Set Side A receive port.
     *
     * @param portA TODO javadoc me please
     */
    public void setPortA(int portA) {
        this.portA = portA;
    }

    /**
     * Get Side B receive port.
     *
     * @return the side B receive port
     */
    public int getPortB() {
        return portB;
    }

    /**
     * Set Side B receive port.
     *
     * @param portB TODO javadoc me please
     */
    public void setPortB(int portB) {
        this.portB = portB;
    }

    /**
     * Get the RTP Bridge IP.
     *
     * @return the RTP Bridge IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * Set the RTP Bridge IP.
     *
     * @param ip TODO javadoc me please
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Get the RTP Agent Pass.
     *
     * @return the RTP Agent Pass
     */
    public String getPass() {
        return pass;
    }

    /**
     * Set the RTP Agent Pass.
     *
     * @param pass TODO javadoc me please
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * Get the name of the Candidate.
     *
     * @return the name of the Candidate
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Candidate.
     *
     * @param name TODO javadoc me please
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the Child Element XML of the Packet
     *
     * @return the Child Element XML of the Packet
     */
    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder str) {
        str.attribute("sid", sid);
        str.rightAngleBracket();

        if (bridgeAction.equals(BridgeAction.create))
            str.append("<candidate/>");
        else if (bridgeAction.equals(BridgeAction.change))
            str.append("<relay ").append(getAttributes()).append(" />");
        else
            str.append("<publicip ").append(getAttributes()).append(" />");

        return str;
    }

    /**
     * IQProvider for RTP Bridge packets.
     * Parse receive RTPBridge stanza to a RTPBridge instance
     *
     * @author Thiago Rocha
     */
    public static class Provider extends IQProvider<RTPBridge> {

        @Override
        public RTPBridge parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment)
                        throws XmlPullParserException,
                        IOException {

            boolean done = false;

            XmlPullParser.Event eventType;
            String elementName;

            if (!parser.getNamespace().equals(RTPBridge.NAMESPACE))
                // TODO: Should be SmackParseException.
                throw new IOException("Not a RTP Bridge packet");

            RTPBridge iq = new RTPBridge();

            for (int i = 0; i < parser.getAttributeCount(); i++) {
                if (parser.getAttributeName(i).equals("sid"))
                    iq.setSid(parser.getAttributeValue(i));
            }

            // Start processing sub-elements
            while (!done) {
                eventType = parser.next();
                elementName = parser.getName();

                if (eventType == XmlPullParser.Event.START_ELEMENT) {
                    if (elementName.equals("candidate")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("ip"))
                                iq.setIp(parser.getAttributeValue(i));
                            else if (parser.getAttributeName(i).equals("pass"))
                                iq.setPass(parser.getAttributeValue(i));
                            else if (parser.getAttributeName(i).equals("name"))
                                iq.setName(parser.getAttributeValue(i));
                            else if (parser.getAttributeName(i).equals("porta"))
                                iq.setPortA(Integer.parseInt(parser.getAttributeValue(i)));
                            else if (parser.getAttributeName(i).equals("portb"))
                                iq.setPortB(Integer.parseInt(parser.getAttributeValue(i)));
                        }
                    }
                    else if (elementName.equals("publicip")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("ip"))
                                iq.setIp(parser.getAttributeValue(i));
                        }
                    }
                }
                else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                    if (parser.getName().equals(RTPBridge.ELEMENT_NAME)) {
                        done = true;
                    }
                }
            }
            return iq;
        }
    }

    /**
     * Get a new RTPBridge Candidate from the server.
     * If a error occurs or the server don't support RTPBridge Service, null is returned.
     *
     * @param connection TODO javadoc me please
     * @param sessionID TODO javadoc me please
     * @return the new RTPBridge
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    @SuppressWarnings("deprecation")
    public static RTPBridge getRTPBridge(XMPPConnection connection, String sessionID) throws NotConnectedException, InterruptedException {

        if (!connection.isConnected()) {
            return null;
        }

        RTPBridge rtpPacket = new RTPBridge(sessionID);
        rtpPacket.setTo(RTPBridge.NAME + "." + connection.getXMPPServiceDomain());

        StanzaCollector collector = connection.createStanzaCollectorAndSend(rtpPacket);

        RTPBridge response = collector.nextResult();

        // Cancel the collector.
        collector.cancel();

        return response;
    }

    /**
     * Check if the server support RTPBridge Service.
     *
     * @param connection TODO javadoc me please
     * @return true if the server supports the RTPBridge service
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public static boolean serviceAvailable(XMPPConnection connection) throws NoResponseException,
                    XMPPErrorException, NotConnectedException, InterruptedException {

        if (!connection.isConnected()) {
            return false;
        }

        LOGGER.fine("Service listing");

        ServiceDiscoveryManager disco = ServiceDiscoveryManager
                .getInstanceFor(connection);
//            DiscoverItems items = disco.discoverItems(connection.getXMPPServiceDomain());
//            Iterator iter = items.getItems();
//            while (iter.hasNext()) {
//                DiscoverItems.Item item = (DiscoverItems.Item) iter.next();
//                if (item.getEntityID().startsWith("rtpbridge.")) {
//                    return true;
//                }
//            }

        DiscoverInfo discoInfo = disco.discoverInfo(connection.getXMPPServiceDomain());
        for (DiscoverInfo.Identity identity : discoInfo.getIdentities()) {
            if (identity.getName() != null && identity.getName().startsWith("rtpbridge")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the server support RTPBridge Service.
     *
     * @param connection TODO javadoc me please
     * @param sessionID the session id.
     * @param pass the password.
     * @param proxyCandidate the proxy candidate.
     * @param localCandidate the local candidate.
     * @return the RTPBridge
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    @SuppressWarnings("deprecation")
    public static RTPBridge relaySession(XMPPConnection connection, String sessionID, String pass, TransportCandidate proxyCandidate, TransportCandidate localCandidate) throws NotConnectedException, InterruptedException {

        if (!connection.isConnected()) {
            return null;
        }

        RTPBridge rtpPacket = new RTPBridge(sessionID, RTPBridge.BridgeAction.change);
        rtpPacket.setTo(RTPBridge.NAME + "." + connection.getXMPPServiceDomain());
        rtpPacket.setType(Type.set);

        rtpPacket.setPass(pass);
        rtpPacket.setPortA(localCandidate.getPort());
        rtpPacket.setPortB(proxyCandidate.getPort());
        rtpPacket.setHostA(localCandidate.getIp());
        rtpPacket.setHostB(proxyCandidate.getIp());

        // LOGGER.debug("Relayed to: " + candidate.getIp() + ":" + candidate.getPort());

        StanzaCollector collector = connection.createStanzaCollectorAndSend(rtpPacket);

        RTPBridge response = collector.nextResult();

        // Cancel the collector.
        collector.cancel();

        return response;
    }

    /**
     * Get Public Address from the Server.
     *
     * @param xmppConnection TODO javadoc me please
     * @return public IP String or null if not found
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    @SuppressWarnings("deprecation")
    public static String getPublicIP(XMPPConnection xmppConnection) throws NotConnectedException, InterruptedException {

        if (!xmppConnection.isConnected()) {
            return null;
        }

        RTPBridge rtpPacket = new RTPBridge(RTPBridge.BridgeAction.publicip);
        rtpPacket.setTo(RTPBridge.NAME + "." + xmppConnection.getXMPPServiceDomain());
        rtpPacket.setType(Type.set);

        // LOGGER.debug("Relayed to: " + candidate.getIp() + ":" + candidate.getPort());

        StanzaCollector collector = xmppConnection.createStanzaCollectorAndSend(rtpPacket);

        RTPBridge response = collector.nextResult();

        // Cancel the collector.
        collector.cancel();

        if (response == null) return null;

        if (response.getIp() == null || response.getIp().equals("")) return null;

        Enumeration<NetworkInterface> ifaces = null;
        try {
            ifaces = NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException e) {
            LOGGER.log(Level.WARNING, "exception", e);
        }
        while (ifaces != null && ifaces.hasMoreElements()) {

            NetworkInterface iface = ifaces.nextElement();
            Enumeration<InetAddress> iaddresses = iface.getInetAddresses();

            while (iaddresses.hasMoreElements()) {
                InetAddress iaddress = iaddresses.nextElement();
                if (!iaddress.isLoopbackAddress())
                    if (iaddress.getHostAddress().indexOf(response.getIp()) >= 0)
                        return null;

            }
        }

        return response.getIp();
    }

}
