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

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.Stanza;

import org.jxmpp.jid.Jid;

/**
 * A class embodying the semantic agent chat offer; specific instances allow the acceptance or
 * rejecting of the offer.<br>
 *
 * @author Matt Tucker
 * @author loki der quaeler
 * @author Derek DeMoro
 */
public class Offer {

    private final XMPPConnection connection;
    private final AgentSession session;

    private final String sessionID;
    private final Jid userJID;
    private final Jid userID;
    private final Jid workgroupName;
    private final Date expiresDate;
    private final Map<String, List<String>> metaData;
    private final OfferContent content;

    private boolean accepted = false;
    private boolean rejected = false;

    /**
     * Creates a new offer.
     *
     * @param conn the XMPP connection with which the issuing session was created.
     * @param agentSession the agent session instance through which this offer was issued.
     * @param userID  the userID of the user from which the offer originates.
     * @param userJID the XMPP address of the user from which the offer originates.
     * @param workgroupName the fully qualified name of the workgroup.
     * @param sessionID the session id associated with the offer.
     * @param metaData the metadata associated with the offer.
     * @param content content of the offer. The content explains the reason for the offer
     *        (e.g. user request, transfer)
     */
    Offer(XMPPConnection conn, AgentSession agentSession, Jid userID,
            Jid userJID, Jid workgroupName, Date expiresDate,
            String sessionID, Map<String, List<String>> metaData, OfferContent content) {
        this.connection = conn;
        this.session = agentSession;
        this.userID = userID;
        this.userJID = userJID;
        this.workgroupName = workgroupName;
        this.expiresDate = expiresDate;
        this.sessionID = sessionID;
        this.metaData = metaData;
        this.content = content;
    }

    /**
     * Accepts the offer.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public void accept() throws NotConnectedException, InterruptedException {
        Stanza acceptPacket = new AcceptPacket(this.session.getWorkgroupJID());
        connection.sendStanza(acceptPacket);
        // TODO: listen for a reply.
        accepted = true;
    }

    /**
     * Rejects the offer.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public void reject() throws NotConnectedException, InterruptedException {
        RejectPacket rejectPacket = new RejectPacket(this.session.getWorkgroupJID());
        connection.sendStanza(rejectPacket);
        // TODO: listen for a reply.
        rejected = true;
    }

    /**
     * Returns the userID that the offer originates from. In most cases, the
     * userID will simply be the JID of the requesting user. However, users can
     * also manually specify a userID for their request. In that case, that value will
     * be returned.
     *
     * @return the userID of the user from which the offer originates.
     */
    public Jid getUserID() {
        return userID;
    }

    /**
     * Returns the JID of the user that made the offer request.
     *
     * @return the user's JID.
     */
    public Jid getUserJID() {
        return userJID;
    }

    /**
     * The fully qualified name of the workgroup (eg support@example.com).
     *
     * @return the name of the workgroup.
     */
    public Jid getWorkgroupName() {
        return this.workgroupName;
    }

    /**
     * The date when the offer will expire. The agent must {@link #accept()}
     * the offer before the expiration date or the offer will lapse and be
     * routed to another agent. Alternatively, the agent can {@link #reject()}
     * the offer at any time if they don't wish to accept it..
     *
     * @return the date at which this offer expires.
     */
    public Date getExpiresDate() {
        return this.expiresDate;
    }

    /**
     * The session ID associated with the offer.
     *
     * @return the session id associated with the offer.
     */
    public String getSessionID() {
        return this.sessionID;
    }

    /**
     * The meta-data associated with the offer.
     *
     * @return the offer meta-data.
     */
    public Map<String, List<String>> getMetaData() {
        return this.metaData;
    }

    /**
     * Returns the content of the offer. The content explains the reason for the offer
     * (e.g. user request, transfer)
     *
     * @return the content of the offer.
     */
    public OfferContent getContent() {
        return content;
    }

    /**
     * Returns true if the agent accepted this offer.
     *
     * @return true if the agent accepted this offer.
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Return true if the agent rejected this offer.
     *
     * @return true if the agent rejected this offer.
     */
    public boolean isRejected() {
        return rejected;
    }

    /**
     * Stanza for rejecting offers.
     */
    private class RejectPacket extends IQ {

        RejectPacket(Jid workgroup) {
            super("offer-reject", "http://jabber.org/protocol/workgroup");
            this.setTo(workgroup);
            this.setType(IQ.Type.set);
        }

        @Override
        protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
            xml.attribute("id", Offer.this.getSessionID());
            xml.setEmptyElement();
            return xml;
        }
    }

    /**
     * Stanza for accepting an offer.
     */
    private class AcceptPacket extends IQ {

        AcceptPacket(Jid workgroup) {
            super("offer-accept", "http://jabber.org/protocol/workgroup");
            this.setTo(workgroup);
            this.setType(IQ.Type.set);
        }

        @Override
        protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
            xml.attribute("id", Offer.this.getSessionID());
            xml.setEmptyElement();
            return xml;
        }
    }

}
