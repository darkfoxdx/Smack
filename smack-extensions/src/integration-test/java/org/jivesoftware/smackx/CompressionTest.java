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

package com.advisoryapps.smackx;

import com.advisoryapps.smack.*;
import com.advisoryapps.smack.filter.PacketIDFilter;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.test.SmackTestCase;
import com.advisoryapps.smackx.packet.Version;

/**
 * Ensure that stream compression (XEP-138) is correctly supported by Smack.
 *
 * @author Gaston Dombiak
 */
public class CompressionTest extends SmackTestCase {

    public CompressionTest(String arg0) {
        super(arg0);
    }

    /**
     * Test that stream compression works fine. It is assumed that the server supports and has
     * stream compression enabled.
     */
    public void testSuccessCompression() throws XMPPException {

        // Create the configuration for this new connection
        ConnectionConfiguration config = new ConnectionConfiguration(getHost(), getPort());
        config.setCompressionEnabled(true);
        config.setSASLAuthenticationEnabled(true);

        XMPPTCPConnection connection = new XMPPConnection(config);
        connection.connect();

        // Login with the test account
        connection.login("user0", "user0");

        assertTrue("XMPPConnection is not using stream compression", connection.isUsingCompression());

        // Request the version of the server
        Version version = new Version();
        version.setType(IQ.Type.get);
        version.setTo(getXMPPServiceDomain());

        // Create a packet collector to listen for a response.
        StanzaCollector collector = connection.createStanzaCollector(new PacketIDFilter(version.getStanzaId()));

        connection.sendStanza(version);

        // Wait up to 5 seconds for a result.
        IQ result = (IQ)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        // Close the collector
        collector.cancel();

        assertNotNull("No reply was received from the server", result);
        assertEquals("Incorrect IQ type from server", IQ.Type.result, result.getType());

        // Close connection
        connection.disconnect();
    }

    protected int getMaxConnections() {
        return 0;
    }

    /**
     * Just create an account.
     */
    protected void setUp() throws Exception {
        super.setUp();
        XMPPTCPConnection setupConnection = new XMPPConnection(getXMPPServiceDomain());
        setupConnection.connect();
        if (!setupConnection.getAccountManager().supportsAccountCreation())
            fail("Server does not support account creation");

        // Create the test account
        try {
            setupConnection.getAccountManager().createAccount("user0", "user0");
        } catch (XMPPException e) {
            // Do nothing if the accout already exists
            if (e.getStanzaError().getCode() != 409) {
                throw e;
            }
        }
    }

    /**
     * Deletes the created account for the test.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        XMPPTCPConnection setupConnection = createConnection();
        setupConnection.connect();
        setupConnection.login("user0", "user0");
        // Delete the created account for the test
        setupConnection.getAccountManager().deleteAccount();
        // Close the setupConnection
        setupConnection.disconnect();
    }
}
