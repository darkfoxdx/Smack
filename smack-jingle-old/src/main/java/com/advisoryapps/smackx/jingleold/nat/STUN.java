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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.StanzaCollector;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException;
import com.advisoryapps.smack.packet.SimpleIQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.provider.ProviderManager;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;
import com.advisoryapps.smackx.disco.packet.DiscoverInfo;
import com.advisoryapps.smackx.disco.packet.DiscoverItems;

/**
 * STUN IQ Stanza used to request and retrieve a STUN server and port to make p2p connections easier. STUN is usually used by Jingle Media Transmission between two parties that are behind NAT.
 *
 * High Level Usage Example:
 *
 * STUN stun = STUN.getSTUNServer(connection);
 *
 * @author Thiago Camargo
 */
public class STUN extends SimpleIQ {

    private static final Logger LOGGER = Logger.getLogger(STUN.class.getName());

    private final List<StunServerAddress> servers = new ArrayList<>();

    private String publicIp = null;

    /**
     * Element name of the stanza extension.
     */
    public static final String DOMAIN = "stun";

    /**
     * Element name of the stanza extension.
     */
    public static final String ELEMENT_NAME = "query";

    /**
     * Namespace of the stanza extension.
     */
    public static final String NAMESPACE = "google:jingleinfo";

    static {
        ProviderManager.addIQProvider(ELEMENT_NAME, NAMESPACE, new STUN.Provider());
    }

    /**
     * Creates a STUN IQ.
     */
    public STUN() {
        super(ELEMENT_NAME, NAMESPACE);
    }

    /**
     * Get a list of STUN Servers recommended by the Server.
     *
     * @return the list of STUN servers
     */
    public List<StunServerAddress> getServers() {
        return servers;
    }

    /**
     * Get Public Ip returned from the XMPP server.
     *
     * @return the public IP
     */
    public String getPublicIp() {
        return publicIp;
    }

    /**
     * Set Public Ip returned from the XMPP server
     *
     * @param publicIp TODO javadoc me please
     */
    private void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    /**
     * IQProvider for RTP Bridge packets.
     * Parse receive RTPBridge stanza to a RTPBridge instance
     *
     * @author Thiago Rocha
     */
    public static class Provider extends IQProvider<STUN> {

        @Override
        public STUN parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment)
                        throws XmlPullParserException,
                        IOException {

            boolean done = false;

            XmlPullParser.Event eventType;
            String elementName;

            if (!parser.getNamespace().equals(NAMESPACE))
                throw new IOException("Not a STUN packet");

            STUN iq = new STUN();

            // Start processing sub-elements
            while (!done) {
                eventType = parser.next();
                elementName = parser.getName();

                if (eventType == XmlPullParser.Event.START_ELEMENT) {
                    if (elementName.equals("server")) {
                        String host = null;
                        String port = null;
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("host"))
                                host = parser.getAttributeValue(i);
                            else if (parser.getAttributeName(i).equals("udp"))
                                port = parser.getAttributeValue(i);
                        }
                        if (host != null && port != null)
                            iq.servers.add(new StunServerAddress(host, port));
                    }
                    else if (elementName.equals("publicip")) {
                        String host = null;
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("ip"))
                                host = parser.getAttributeValue(i);
                        }
                        if (host != null && !host.equals(""))
                            iq.setPublicIp(host);
                    }
                }
                else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                    if (parser.getName().equals(ELEMENT_NAME)) {
                        done = true;
                    }
                }
            }
            return iq;
        }
    }

    /**
     * Get a new STUN Server Address and port from the server.
     * If a error occurs or the server don't support STUN Service, null is returned.
     *
     * @param connection TODO javadoc me please
     * @return the STUN server address
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    @SuppressWarnings("deprecation")
    public static STUN getSTUNServer(XMPPConnection connection) throws NotConnectedException, InterruptedException {

        if (!connection.isConnected()) {
            return null;
        }

        STUN stunPacket = new STUN();
        stunPacket.setTo(DOMAIN + "." + connection.getXMPPServiceDomain());

        StanzaCollector collector = connection.createStanzaCollectorAndSend(stunPacket);

        STUN response = collector.nextResult();

        // Cancel the collector.
        collector.cancel();

        return response;
    }

    /**
     * Check if the server support STUN Service.
     *
     * @param connection the connection
     * @return true if the server support STUN
     * @throws SmackException if Smack detected an exceptional situation.
     * @throws XMPPException if an XMPP protocol error was received.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public static boolean serviceAvailable(XMPPConnection connection) throws XMPPException, SmackException, InterruptedException {

        if (!connection.isConnected()) {
            return false;
        }

        LOGGER.fine("Service listing");

        ServiceDiscoveryManager disco = ServiceDiscoveryManager.getInstanceFor(connection);
        DiscoverItems items = disco.discoverItems(connection.getXMPPServiceDomain());

        for (DiscoverItems.Item item : items.getItems()) {
            DiscoverInfo info = disco.discoverInfo(item.getEntityID());

            for (DiscoverInfo.Identity identity : info.getIdentities()) {
                if (identity.getCategory().equals("proxy") && identity.getType().equals("stun"))
                    if (info.containsFeature(NAMESPACE))
                        return true;
            }

            LOGGER.fine(item.getName() + "-" + info.getType());

        }

        return false;
    }

    /**
     * Provides easy abstract to store STUN Server Addresses and Ports.
     */
    public static class StunServerAddress {

        private String server;
        private String port;

        public StunServerAddress(String server, String port) {
            this.server = server;
            this.port = port;
        }

        /**
         * Get the Host Address.
         *
         * @return the host address
         */
        public String getServer() {
            return server;
        }

        /**
         * Get the Server Port.
         *
         * @return the server port
         */
        public String getPort() {
            return port;
        }
    }
}
