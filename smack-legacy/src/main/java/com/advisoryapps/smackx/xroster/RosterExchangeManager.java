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
package com.advisoryapps.smackx.xroster;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.StanzaListener;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.filter.StanzaExtensionFilter;
import com.advisoryapps.smack.filter.StanzaFilter;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.packet.Stanza;
import com.advisoryapps.smack.roster.Roster;
import com.advisoryapps.smack.roster.RosterEntry;
import com.advisoryapps.smack.roster.RosterGroup;

import com.advisoryapps.smackx.xroster.packet.RosterExchange;

import org.jxmpp.jid.Jid;

/**
 *
 * Manages Roster exchanges. A RosterExchangeManager provides a high level access to send
 * rosters, roster groups and roster entries to XMPP clients. It also provides an easy way
 * to hook up custom logic when entries are received from another XMPP client through
 * RosterExchangeListeners.
 *
 * @author Gaston Dombiak
 */
public class RosterExchangeManager {

    public static final String NAMESPACE = "jabber:x:roster";
    public static final String ELEMENT = "x";

    private static final Map<XMPPConnection, RosterExchangeManager> INSTANCES = new WeakHashMap<>();

    private static final StanzaFilter PACKET_FILTER = new StanzaExtensionFilter(ELEMENT, NAMESPACE);

    private final Set<RosterExchangeListener> rosterExchangeListeners = Collections.synchronizedSet(new HashSet<RosterExchangeListener>());

    private final WeakReference<XMPPConnection> weakRefConnection;
    private final StanzaListener packetListener;

    public static synchronized RosterExchangeManager getInstanceFor(XMPPConnection connection) {
        RosterExchangeManager rosterExchangeManager = INSTANCES.get(connection);
        if (rosterExchangeManager == null) {
            rosterExchangeManager = new RosterExchangeManager(connection);
            INSTANCES.put(connection, rosterExchangeManager);
        }
        return rosterExchangeManager;
    }

    /**
     * Creates a new roster exchange manager.
     *
     * @param connection an XMPPConnection which is used to send and receive messages.
     */
    public RosterExchangeManager(XMPPConnection connection) {
        weakRefConnection = new WeakReference<>(connection);
        // Listens for all roster exchange packets and fire the roster exchange listeners.
        packetListener = new StanzaListener() {
            @Override
            public void processStanza(Stanza packet) {
                Message message = (Message) packet;
                RosterExchange rosterExchange = message.getExtension(ELEMENT, NAMESPACE);
                // Fire event for roster exchange listeners
                fireRosterExchangeListeners(message.getFrom(), rosterExchange.getRosterEntries());
            }
        };
        connection.addAsyncStanzaListener(packetListener, PACKET_FILTER);
    }

    /**
     * Adds a listener to roster exchanges. The listener will be fired anytime roster entries
     * are received from remote XMPP clients.
     *
     * @param rosterExchangeListener a roster exchange listener.
     */
    public void addRosterListener(RosterExchangeListener rosterExchangeListener) {
        rosterExchangeListeners.add(rosterExchangeListener);
    }

    /**
     * Removes a listener from roster exchanges. The listener will be fired anytime roster
     * entries are received from remote XMPP clients.
     *
     * @param rosterExchangeListener a roster exchange listener..
     */
    public void removeRosterListener(RosterExchangeListener rosterExchangeListener) {
        rosterExchangeListeners.remove(rosterExchangeListener);
    }

    /**
     * Sends a roster to userID. All the entries of the roster will be sent to the
     * target user.
     *
     * @param roster the roster to send
     * @param targetUserID the user that will receive the roster entries
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public void send(Roster roster, Jid targetUserID) throws NotConnectedException, InterruptedException {
        // Create a new message to send the roster
        Message msg = new Message(targetUserID);
        // Create a RosterExchange Package and add it to the message
        RosterExchange rosterExchange = new RosterExchange(roster);
        msg.addExtension(rosterExchange);

        XMPPConnection connection = weakRefConnection.get();
        // Send the message that contains the roster
        connection.sendStanza(msg);
    }

    /**
     * Sends a roster entry to userID.
     *
     * @param rosterEntry the roster entry to send
     * @param targetUserID the user that will receive the roster entries
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public void send(RosterEntry rosterEntry, Jid targetUserID) throws NotConnectedException, InterruptedException {
        // Create a new message to send the roster
        Message msg = new Message(targetUserID);
        // Create a RosterExchange Package and add it to the message
        RosterExchange rosterExchange = new RosterExchange();
        rosterExchange.addRosterEntry(rosterEntry);
        msg.addExtension(rosterExchange);

        XMPPConnection connection = weakRefConnection.get();
        // Send the message that contains the roster
        connection.sendStanza(msg);
    }

    /**
     * Sends a roster group to userID. All the entries of the group will be sent to the
     * target user.
     *
     * @param rosterGroup the roster group to send
     * @param targetUserID the user that will receive the roster entries
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public void send(RosterGroup rosterGroup, Jid targetUserID) throws NotConnectedException, InterruptedException {
        // Create a new message to send the roster
        Message msg = new Message(targetUserID);
        // Create a RosterExchange Package and add it to the message
        RosterExchange rosterExchange = new RosterExchange();
        for (RosterEntry entry : rosterGroup.getEntries()) {
            rosterExchange.addRosterEntry(entry);
        }
        msg.addExtension(rosterExchange);

        XMPPConnection connection = weakRefConnection.get();
        // Send the message that contains the roster
        connection.sendStanza(msg);
    }

    /**
     * Fires roster exchange listeners.
     */
    private void fireRosterExchangeListeners(Jid from, Iterator<RemoteRosterEntry> remoteRosterEntries) {
        RosterExchangeListener[] listeners;
        synchronized (rosterExchangeListeners) {
            listeners = new RosterExchangeListener[rosterExchangeListeners.size()];
            rosterExchangeListeners.toArray(listeners);
        }
        for (RosterExchangeListener listener : listeners) {
            listener.entriesReceived(from, remoteRosterEntries);
        }
    }
}
