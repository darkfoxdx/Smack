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
package com.advisoryapps.smackx.bytestreams.ibb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.StanzaError;

import com.advisoryapps.smackx.InitExtensions;
import com.advisoryapps.smackx.bytestreams.ibb.InBandBytestreamManager.StanzaType;
import com.advisoryapps.smackx.bytestreams.ibb.packet.Open;

import com.advisoryapps.util.ConnectionUtils;
import com.advisoryapps.util.Protocol;
import com.advisoryapps.util.Verification;
import org.junit.Before;
import org.junit.Test;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.JidTestUtil;

/**
 * Test for InBandBytestreamManager.
 *
 * @author Henning Staib
 */
public class InBandBytestreamManagerTest extends InitExtensions {

    // settings
    private static final EntityFullJid initiatorJID = JidTestUtil.DUMMY_AT_EXAMPLE_ORG_SLASH_DUMMYRESOURCE;
    private static final EntityFullJid targetJID = JidTestUtil.FULL_JID_1_RESOURCE_1;
    String sessionID = "session_id";

    // protocol verifier
    private Protocol protocol;

    // mocked XMPP connection
    private XMPPConnection connection;

    /**
     * Initialize fields used in the tests.
     * @throws XMPPException if an XMPP protocol error was received.
     * @throws SmackException if Smack detected an exceptional situation.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    @Before
    public void setup() throws XMPPException, SmackException, InterruptedException {

        // build protocol verifier
        protocol = new Protocol();

        // create mocked XMPP connection
        connection = ConnectionUtils.createMockedConnection(protocol, initiatorJID);

    }

    /**
     * Test that
     * {@link InBandBytestreamManager#getByteStreamManager(XMPPConnection)} returns
     * one bytestream manager for every connection.
     */
    @Test
    public void shouldHaveOneManagerForEveryConnection() {

        // mock two connections
        XMPPConnection connection1 = mock(XMPPConnection.class);
        XMPPConnection connection2 = mock(XMPPConnection.class);

        // get bytestream manager for the first connection twice
        InBandBytestreamManager conn1ByteStreamManager1 = InBandBytestreamManager.getByteStreamManager(connection1);
        InBandBytestreamManager conn1ByteStreamManager2 = InBandBytestreamManager.getByteStreamManager(connection1);

        // get bytestream manager for second connection
        InBandBytestreamManager conn2ByteStreamManager1 = InBandBytestreamManager.getByteStreamManager(connection2);

        // assertions
        assertEquals(conn1ByteStreamManager1, conn1ByteStreamManager2);
        assertNotSame(conn1ByteStreamManager1, conn2ByteStreamManager1);

    }

    /**
     * Invoking {@link InBandBytestreamManager#establishSession(org.jxmpp.jid.Jid)} should
     * throw an exception if the given target does not support in-band
     * bytestream.
     * @throws SmackException if Smack detected an exceptional situation.
     * @throws XMPPException if an XMPP protocol error was received.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    @Test
    public void shouldFailIfTargetDoesNotSupportIBB() throws SmackException, XMPPException, InterruptedException {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);

        try {
            IQ errorIQ = IBBPacketUtils.createErrorIQ(targetJID, initiatorJID,
                            StanzaError.Condition.feature_not_implemented);
            protocol.addResponse(errorIQ);

            // start In-Band Bytestream
            byteStreamManager.establishSession(targetJID);

            fail("exception should be thrown");
        }
        catch (XMPPErrorException e) {
            assertEquals(StanzaError.Condition.feature_not_implemented,
                            e.getStanzaError().getCondition());
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowTooBigDefaultBlockSize() {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        byteStreamManager.setDefaultBlockSize(1000000);
    }

    @Test
    public void shouldCorrectlySetDefaultBlockSize() {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        byteStreamManager.setDefaultBlockSize(1024);
        assertEquals(1024, byteStreamManager.getDefaultBlockSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowTooBigMaximumBlockSize() {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        byteStreamManager.setMaximumBlockSize(1000000);
    }

    @Test
    public void shouldCorrectlySetMaximumBlockSize() {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        byteStreamManager.setMaximumBlockSize(1024);
        assertEquals(1024, byteStreamManager.getMaximumBlockSize());
    }

    @Test
    public void shouldUseConfiguredStanzaType() throws SmackException, InterruptedException, XMPPException {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        byteStreamManager.setStanza(StanzaType.MESSAGE);

        protocol.addResponse(null, new Verification<Open, IQ>() {

            @Override
            public void verify(Open request, IQ response) {
                assertEquals(StanzaType.MESSAGE, request.getStanza());
            }

        });

        // start In-Band Bytestream
        byteStreamManager.establishSession(targetJID);

        protocol.verifyAll();
    }

    @Test
    public void shouldReturnSession() throws Exception {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);

        IQ success = IBBPacketUtils.createResultIQ(targetJID, initiatorJID);
        protocol.addResponse(success, Verification.correspondingSenderReceiver,
                        Verification.requestTypeSET);

        // start In-Band Bytestream
        InBandBytestreamSession session = byteStreamManager.establishSession(targetJID);

        assertNotNull(session);
        assertNotNull(session.getInputStream());
        assertNotNull(session.getOutputStream());

        protocol.verifyAll();

    }

}
