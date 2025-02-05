/**
 *
 * Copyright 2003-2007 Jive Software, 2015-2019 Florian Schmaus
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

package com.advisoryapps.smackx.pep;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.advisoryapps.smack.AsyncButOrdered;
import com.advisoryapps.smack.Manager;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.StanzaListener;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;
import com.advisoryapps.smack.filter.AndFilter;
import com.advisoryapps.smack.filter.StanzaFilter;
import com.advisoryapps.smack.filter.jidtype.AbstractJidTypeFilter.JidType;
import com.advisoryapps.smack.filter.jidtype.FromJidTypeFilter;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.packet.Stanza;

import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;
import com.advisoryapps.smackx.pubsub.EventElement;
import com.advisoryapps.smackx.pubsub.Item;
import com.advisoryapps.smackx.pubsub.LeafNode;
import com.advisoryapps.smackx.pubsub.PubSubException.NotALeafNodeException;
import com.advisoryapps.smackx.pubsub.PubSubFeature;
import com.advisoryapps.smackx.pubsub.PubSubManager;
import com.advisoryapps.smackx.pubsub.filter.EventExtensionFilter;

import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;

/**
 *
 * Manages Personal Event Publishing (XEP-163). A PEPManager provides a high level access to
 * PubSub personal events. It also provides an easy way
 * to hook up custom logic when events are received from another XMPP client through PEPListeners.
 *
 * Use example:
 *
 * <pre>
 *   PepManager pepManager = PepManager.getInstanceFor(smackConnection);
 *   pepManager.addPepListener(new PepListener() {
 *       public void eventReceived(EntityBareJid from, EventElement event, Message message) {
 *           LOGGER.debug("Event received: " + event);
 *       }
 *   });
 * </pre>
 *
 * @author Jeff Williams
 * @author Florian Schmaus
 */
public final class PepManager extends Manager {

    private static final Map<XMPPConnection, PepManager> INSTANCES = new WeakHashMap<>();

    public static synchronized PepManager getInstanceFor(XMPPConnection connection) {
        PepManager pepManager = INSTANCES.get(connection);
        if (pepManager == null) {
            pepManager = new PepManager(connection);
            INSTANCES.put(connection, pepManager);
        }
        return pepManager;
    }

    private static final StanzaFilter FROM_BARE_JID_WITH_EVENT_EXTENSION_FILTER = new AndFilter(
            new FromJidTypeFilter(JidType.BareJid),
            EventExtensionFilter.INSTANCE);

    private final Set<PepListener> pepListeners = new CopyOnWriteArraySet<>();

    private final AsyncButOrdered<EntityBareJid> asyncButOrdered = new AsyncButOrdered<>();

    private final PubSubManager pepPubSubManager;

    /**
     * Creates a new PEP exchange manager.
     *
     * @param connection an XMPPConnection which is used to send and receive messages.
     */
    private PepManager(XMPPConnection connection) {
        super(connection);
        StanzaListener packetListener = new StanzaListener() {
            @Override
            public void processStanza(Stanza stanza) {
                final Message message = (Message) stanza;
                final EventElement event = EventElement.from(stanza);
                assert event != null;
                final EntityBareJid from = message.getFrom().asEntityBareJidIfPossible();
                assert from != null;
                asyncButOrdered.performAsyncButOrdered(from, new Runnable() {
                    @Override
                    public void run() {
                        for (PepListener listener : pepListeners) {
                            listener.eventReceived(from, event, message);
                        }
                    }
                });
            }
        };
        // TODO Add filter to check if from supports PubSub as per xep163 2 2.4
        connection.addSyncStanzaListener(packetListener, FROM_BARE_JID_WITH_EVENT_EXTENSION_FILTER);

        pepPubSubManager = PubSubManager.getInstanceFor(connection, null);
    }

    public PubSubManager getPepPubSubManager() {
        return pepPubSubManager;
    }

    /**
     * Adds a listener to PEPs. The listener will be fired anytime PEP events
     * are received from remote XMPP clients.
     *
     * @param pepListener a roster exchange listener.
     * @return true if pepListener was added.
     */
    public boolean addPepListener(PepListener pepListener) {
        return pepListeners.add(pepListener);
    }

    /**
     * Removes a listener from PEP events.
     *
     * @param pepListener a roster exchange listener.
     * @return true, if pepListener was removed.
     */
    public boolean removePepListener(PepListener pepListener) {
        return pepListeners.remove(pepListener);
    }

    /**
     * Publish an event.
     *
     * @param nodeId the ID of the node to publish on.
     * @param item the item to publish.
     * @return the leaf node the item was published on.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotALeafNodeException if a PubSub leaf node operation was attempted on a non-leaf node.
     */
    public LeafNode publish(String nodeId, Item item) throws NotConnectedException, InterruptedException,
                    NoResponseException, XMPPErrorException, NotALeafNodeException {
        // PEP nodes are auto created if not existent. Hence Use PubSubManager.tryToPublishAndPossibleAutoCreate() here.
        return pepPubSubManager.tryToPublishAndPossibleAutoCreate(nodeId, item);
    }

    /**
     * XEP-163 5.
     */
    private static final PubSubFeature[] REQUIRED_FEATURES = new PubSubFeature[] {
        // @formatter:off
        PubSubFeature.auto_create,
        PubSubFeature.auto_subscribe,
        PubSubFeature.filtered_notifications,
        // @formatter:on
    };

    public boolean isSupported() throws NoResponseException, XMPPErrorException,
                    NotConnectedException, InterruptedException {
        XMPPConnection connection = connection();
        ServiceDiscoveryManager serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);
        BareJid localBareJid = connection.getUser().asBareJid();
        return serviceDiscoveryManager.supportsFeatures(localBareJid, REQUIRED_FEATURES);
    }
}
