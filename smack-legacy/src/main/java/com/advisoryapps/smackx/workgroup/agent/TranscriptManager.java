/**
 *
 * Copyright 2003-2007 Jive Software.
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

package com.advisoryapps.smackx.workgroup.agent;

import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;

import com.advisoryapps.smackx.workgroup.packet.Transcript;
import com.advisoryapps.smackx.workgroup.packet.Transcripts;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

/**
 * A TranscriptManager helps to retrieve the full conversation transcript of a given session
 * {@link #getTranscript(EntityBareJid, String)} or to retrieve a list with the summary of all the
 * conversations that a user had {@link #getTranscripts(EntityBareJid, Jid)}.
 *
 * @author Gaston Dombiak
 */
public class TranscriptManager {
    private XMPPConnection connection;

    public TranscriptManager(XMPPConnection connection) {
        this.connection = connection;
    }

    /**
     * Returns the full conversation transcript of a given session.
     *
     * @param sessionID the id of the session to get the full transcript.
     * @param workgroupJID the JID of the workgroup that will process the request.
     * @return the full conversation transcript of a given session.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public Transcript getTranscript(EntityBareJid workgroupJID, String sessionID) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Transcript request = new Transcript(sessionID);
        request.setTo(workgroupJID);
        Transcript response = connection.createStanzaCollectorAndSend(request).nextResultOrThrow();
        return response;
    }

    /**
     * Returns the transcripts of a given user. The answer will contain the complete history of
     * conversations that a user had.
     *
     * @param userID the id of the user to get his conversations.
     * @param workgroupJID the JID of the workgroup that will process the request.
     * @return the transcripts of a given user.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public Transcripts getTranscripts(EntityBareJid workgroupJID, Jid userID) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        Transcripts request = new Transcripts(userID);
        request.setTo(workgroupJID);
        Transcripts response = connection.createStanzaCollectorAndSend(request).nextResultOrThrow();
        return response;
    }
}
