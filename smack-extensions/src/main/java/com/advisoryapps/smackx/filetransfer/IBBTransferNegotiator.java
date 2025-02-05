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
package com.advisoryapps.smackx.filetransfer;

import java.io.InputStream;
import java.io.OutputStream;

import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;
import com.advisoryapps.smack.packet.Stanza;

import com.advisoryapps.smackx.bytestreams.ibb.InBandBytestreamManager;
import com.advisoryapps.smackx.bytestreams.ibb.InBandBytestreamRequest;
import com.advisoryapps.smackx.bytestreams.ibb.InBandBytestreamSession;
import com.advisoryapps.smackx.bytestreams.ibb.packet.DataPacketExtension;
import com.advisoryapps.smackx.bytestreams.ibb.packet.Open;
import com.advisoryapps.smackx.si.packet.StreamInitiation;

import org.jxmpp.jid.Jid;

/**
 * The In-Band Bytestream file transfer method, or IBB for short, transfers the
 * file over the same XML Stream used by XMPP. It is the fall-back mechanism in
 * case the SOCKS5 bytestream method of transferring files is not available.
 *
 * @author Alexander Wenckus
 * @author Henning Staib
 * @see <a href="http://xmpp.org/extensions/xep-0047.html">XEP-0047: In-Band
 *      Bytestreams (IBB)</a>
 */
public class IBBTransferNegotiator extends StreamNegotiator {

    private final InBandBytestreamManager manager;

    /**
     * The default constructor for the In-Band Bytestream Negotiator.
     *
     * @param connection The connection which this negotiator works on.
     */
    protected IBBTransferNegotiator(XMPPConnection connection) {
        super(connection);
        this.manager = InBandBytestreamManager.getByteStreamManager(connection);
    }

    @Override
    public OutputStream createOutgoingStream(String streamID, Jid initiator,
                    Jid target) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        InBandBytestreamSession session = this.manager.establishSession(target, streamID);
        session.setCloseBothStreamsEnabled(true);
        return session.getOutputStream();
    }

    @Override
    public InputStream createIncomingStream(StreamInitiation initiation)
                    throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        /*
         * In-Band Bytestream initiation listener must ignore next in-band bytestream request with
         * given session ID
         */
        this.manager.ignoreBytestreamRequestOnce(initiation.getSessionID());

        Stanza streamInitiation = initiateIncomingStream(connection(), initiation);
        return negotiateIncomingStream(streamInitiation);
    }

    @Override
    public void newStreamInitiation(Jid from, String streamID) {
        /*
         * this method is always called prior to #negotiateIncomingStream() so
         * the In-Band Bytestream initiation listener must ignore the next
         * In-Band Bytestream request with the given session ID
         */
        this.manager.ignoreBytestreamRequestOnce(streamID);
    }

    @Override
    public String[] getNamespaces() {
        return new String[] { DataPacketExtension.NAMESPACE };
    }

    @Override
    InputStream negotiateIncomingStream(Stanza streamInitiation) throws NotConnectedException, InterruptedException {
        // build In-Band Bytestream request
        InBandBytestreamRequest request = new ByteStreamRequest(this.manager,
                        (Open) streamInitiation);

        // always accept the request
        InBandBytestreamSession session = request.accept();
        session.setCloseBothStreamsEnabled(true);
        return session.getInputStream();
    }

    /**
     * Derive from InBandBytestreamRequest to access protected constructor.
     */
    private static final class ByteStreamRequest extends InBandBytestreamRequest {

        private ByteStreamRequest(InBandBytestreamManager manager, Open byteStreamRequest) {
            super(manager, byteStreamRequest);
        }

    }

}
