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
package com.advisoryapps.smackx.jingleold;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException;
import com.advisoryapps.smack.packet.IQ;

import com.advisoryapps.smackx.jingleold.listeners.JingleListener;

/**
 * Basic Jingle negotiator.
 * <p>
 * JingleNegotiator implements some basic behavior for every Jingle negotiation.
 * It implements a "state" pattern: each stage should process Jingle packets and
 * act depending on the current state in the negotiation...
 * </p>
 *
 * @author Alvaro Saurin
 * @author Jeff Williams
 */
public abstract class JingleNegotiator {

    private static final Logger LOGGER = Logger.getLogger(JingleNegotiator.class.getName());

    // private XMPPConnection connection; // The connection associated

    protected JingleSession session;

    private final List<JingleListener> listeners = new ArrayList<>();

    private String expectedAckId;

    private JingleNegotiatorState state;

    private boolean isStarted;

    /**
     * Default constructor.
     */
    public JingleNegotiator() {
        this(null);
    }

    /**
     * Default constructor with a Connection.
     *
     * @param session the jingle session
     */
    public JingleNegotiator(JingleSession session) {
        this.session = session;
        state = JingleNegotiatorState.PENDING;
    }

    public JingleNegotiatorState getNegotiatorState() {
        return state;
    }

    public void setNegotiatorState(JingleNegotiatorState stateIs) {

        JingleNegotiatorState stateWas = state;

        LOGGER.fine("Negotiator state change: " + stateWas + "->" + stateIs  + "(" + this.getClass().getSimpleName() + ")");

        switch (stateIs) {
            case PENDING:
                break;

            case FAILED:
                break;

            case SUCCEEDED:
                break;

            default:
                break;
        }

       this.state = stateIs;
    }

    public XMPPConnection getConnection() {
        if (session != null) {
            return session.getConnection();
        } else {
            return null;
        }
    }

    /**
      * Get the XMPP connection associated with this negotiation.
      *
      * @return the connection
      */
    public JingleSession getSession() {
        return session;
    }

    /**
     * Set the XMPP connection associated.
     *
     * @param session the jingle session
     */
    public void setSession(JingleSession session) {
        this.session = session;
    }

    // Acks management

    /**
     * Add expected ID.
     *
     * @param id TODO javadoc me please
     */
    public void addExpectedId(String id) {
        expectedAckId = id;
    }

    /**
     * Check if the passed ID is the expected ID.
     *
     * @param id TODO javadoc me please
     * @return true if is expected id
     */
    public boolean isExpectedId(String id) {
        if (id != null) {
            return id.equals(expectedAckId);
        } else {
            return false;
        }
    }

    /**
     * Remove and expected ID.
     *
     * @param id TODO javadoc me please
     */
    public void removeExpectedId(String id) {
        addExpectedId(null);
    }

    // Listeners

    /**
     * Add a Jingle session listener to listen to incoming session requests.
     *
     * @param li The listener
     * @see com.advisoryapps.smackx.jingleold.listeners.JingleListener
     */
    public void addListener(JingleListener li) {
        synchronized (listeners) {
            listeners.add(li);
        }
    }

    /**
     * Removes a Jingle session listener.
     *
     * @param li The jingle session listener to be removed
     * @see com.advisoryapps.smackx.jingleold.listeners.JingleListener
     */
    public void removeListener(JingleListener li) {
        synchronized (listeners) {
            listeners.remove(li);
        }
    }

    /**
     * Get a copy of the listeners
     *
     * @return a copy of the listeners
     */
    protected List<JingleListener> getListenersList() {
        ArrayList<JingleListener> result;

        synchronized (listeners) {
            result = new ArrayList<>(listeners);
        }

        return result;
    }

    /**
     * Dispatch an incoming packet.
     *
     * The negotiators form a tree relationship that roughly matches the Jingle stanza format:
     *
     * JingleSession
     *      Content Negotiator
     *          Media Negotiator
     *          Transport Negotiator
     *      Content Negotiator
     *          Media Negotiator
     *          Transport Negotiator
     *
     * &lt;jingle&gt;
     *      &lt;content&gt;
     *          &lt;description&gt;
     *          &lt;transport&gt;
     *      &lt;content&gt;
     *          &lt;description&gt;
     *          &lt;transport&gt;
     *
     * This way, each segment of a Jingle stanza has a corresponding negotiator that know how to deal with that
     * part of the Jingle packet.  It also allows us to support Jingle packets of arbitraty complexity.
     *
     * Each parent calls dispatchIncomingPacket for each of its children.  The children then pass back a List of
     * results that will get sent when we reach the top level negotiator (JingleSession).
     *
     * @param iq the stanza received
     * @param id the ID of the response that will be sent
     * @return the new stanza to send (either a Jingle or an IQ error).
     * @throws XMPPException if an XMPP protocol error was received.
     * @throws SmackException if Smack detected an exceptional situation.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public abstract List<IQ> dispatchIncomingPacket(IQ iq, String id) throws XMPPException, SmackException, InterruptedException;

    // CHECKSTYLE:OFF
    public void start() {
        isStarted = true;
        doStart();
    }

    public boolean isStarted() {
        return isStarted;
    }
    // CHECKSTYLE:ON

    /**
     * Each of the negotiators has their individual behavior when they start.
     */
    protected abstract void doStart();

    /**
     * Close the negotiation.
     */
    public void close() {

    }
}
