/**
 *
 * Copyright 2013-2014 Georg Lukas, 2017-2018 Florian Schmaus
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
package com.advisoryapps.smackx.carbons;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.advisoryapps.smack.AbstractConnectionListener;
import com.advisoryapps.smack.AsyncButOrdered;
import com.advisoryapps.smack.ConnectionCreationListener;
import com.advisoryapps.smack.Manager;
import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.SmackFuture;
import com.advisoryapps.smack.StanzaListener;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPConnectionRegistry;
import com.advisoryapps.smack.XMPPException;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;
import com.advisoryapps.smack.filter.AndFilter;
import com.advisoryapps.smack.filter.FromMatchesFilter;
import com.advisoryapps.smack.filter.OrFilter;
import com.advisoryapps.smack.filter.StanzaExtensionFilter;
import com.advisoryapps.smack.filter.StanzaFilter;
import com.advisoryapps.smack.filter.StanzaTypeFilter;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.packet.Stanza;
import com.advisoryapps.smack.util.ExceptionCallback;
import com.advisoryapps.smack.util.SuccessCallback;

import com.advisoryapps.smackx.carbons.packet.Carbon;
import com.advisoryapps.smackx.carbons.packet.CarbonExtension;
import com.advisoryapps.smackx.carbons.packet.CarbonExtension.Direction;
import com.advisoryapps.smackx.carbons.packet.CarbonExtension.Private;
import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;
import com.advisoryapps.smackx.forward.packet.Forwarded;

import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityFullJid;

/**
 * Manager for XEP-0280: Message Carbons. This class implements the manager for registering {@link CarbonExtension}
 * support, enabling and disabling message carbons, and for {@link CarbonCopyReceivedListener}.
 * <p>
 * Note that <b>it is important to match the 'from' attribute of the message wrapping a carbon copy</b>, as otherwise it would
 * may be possible for others to impersonate users. Smack's CarbonManager takes care of that in
 * {@link CarbonCopyReceivedListener}s which were registered with
 * {@link #addCarbonCopyReceivedListener(CarbonCopyReceivedListener)}.
 * </p>
 * <p>
 * You should call enableCarbons() before sending your first undirected presence (aka. the "initial presence").
 * </p>
 *
 * @author Georg Lukas
 * @author Florian Schmaus
 */
public final class CarbonManager extends Manager {

    private static Map<XMPPConnection, CarbonManager> INSTANCES = new WeakHashMap<XMPPConnection, CarbonManager>();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                getInstanceFor(connection);
            }
        });
    }

    private static final StanzaFilter CARBON_EXTENSION_FILTER =
                    // @formatter:off
                    new AndFilter(
                        new OrFilter(
                            new StanzaExtensionFilter(CarbonExtension.Direction.sent.name(), CarbonExtension.NAMESPACE),
                            new StanzaExtensionFilter(CarbonExtension.Direction.received.name(), CarbonExtension.NAMESPACE)
                        ),
                        StanzaTypeFilter.MESSAGE
                    );
                    // @formatter:on

    private final Set<CarbonCopyReceivedListener> listeners = new CopyOnWriteArraySet<>();

    private volatile boolean enabled_state = false;

    private final StanzaListener carbonsListener;

    private final AsyncButOrdered<BareJid> carbonsListenerAsyncButOrdered = new AsyncButOrdered<>();

    private CarbonManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        sdm.addFeature(CarbonExtension.NAMESPACE);

        carbonsListener = new StanzaListener() {
            @Override
            public void processStanza(final Stanza stanza) throws NotConnectedException, InterruptedException {
                final Message wrappingMessage = (Message) stanza;
                final CarbonExtension carbonExtension = CarbonExtension.from(wrappingMessage);
                final Direction direction = carbonExtension.getDirection();
                final Forwarded forwarded = carbonExtension.getForwarded();
                final Message carbonCopy = (Message) forwarded.getForwardedStanza();
                final BareJid from = carbonCopy.getFrom().asBareJid();

                carbonsListenerAsyncButOrdered.performAsyncButOrdered(from, new Runnable() {
                    @Override
                    public void run() {
                        for (CarbonCopyReceivedListener listener : listeners) {
                            listener.onCarbonCopyReceived(direction, carbonCopy, wrappingMessage);
                        }
                    }
                });
            }
        };

        connection.addConnectionListener(new AbstractConnectionListener() {
            @Override
            public void connectionClosed() {
                // Reset the state if the connection was cleanly closed. Note that this is not strictly necessary,
                // because we also reset in authenticated() if the stream got not resumed, but for maximum correctness,
                // also reset here.
                enabled_state = false;
                boolean removed = connection().removeSyncStanzaListener(carbonsListener);
                assert removed;
            }
            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                if (!resumed) {
                    // Non-resumed XMPP sessions always start with disabled carbons
                    enabled_state = false;
                }
                addCarbonsListener(connection);
            }
        });

        addCarbonsListener(connection);
    }

    private void addCarbonsListener(XMPPConnection connection) {
        EntityFullJid localAddress = connection.getUser();
        if (localAddress == null) {
            // We where not connected yet and thus we don't know our XMPP address at the moment, which we need to match incoming
            // carbons securely. Abort here. The ConnectionListener above will eventually setup the carbons listener.
            return;
        }

        // XEP-0280 § 11. Security Considerations "Any forwarded copies received by a Carbons-enabled client MUST be
        // from that user's bare JID; any copies that do not meet this requirement MUST be ignored." Otherwise, if
        // those copies do not get ignored, malicious users may be able to impersonate other users. That is why the
        // 'from' matcher is important here.
        connection.addSyncStanzaListener(carbonsListener, new AndFilter(CARBON_EXTENSION_FILTER,
                        FromMatchesFilter.createBare(localAddress)));
    }

    /**
     * Obtain the CarbonManager responsible for a connection.
     *
     * @param connection the connection object.
     *
     * @return a CarbonManager instance
     */
    public static synchronized CarbonManager getInstanceFor(XMPPConnection connection) {
        CarbonManager carbonManager = INSTANCES.get(connection);

        if (carbonManager == null) {
            carbonManager = new CarbonManager(connection);
            INSTANCES.put(connection, carbonManager);
        }

        return carbonManager;
    }

    private static IQ carbonsEnabledIQ(final boolean new_state) {
        IQ request;
        if (new_state) {
            request = new Carbon.Enable();
        } else {
            request = new Carbon.Disable();
        }
        return request;
    }

    /**
     * Add a carbon copy received listener.
     *
     * @param listener the listener to register.
     * @return <code>true</code> if the filter was not already registered.
     * @since 4.2
     */
    public boolean addCarbonCopyReceivedListener(CarbonCopyReceivedListener listener) {
        return listeners.add(listener);
    }

    /**
     * Remove a carbon copy received listener.
     *
     * @param listener the listener to register.
     * @return <code>true</code> if the filter was registered.
     * @since 4.2
     */
    public boolean removeCarbonCopyReceivedListener(CarbonCopyReceivedListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Returns true if XMPP Carbons are supported by the server.
     *
     * @return true if supported
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public boolean isSupportedByServer() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).serverSupportsFeature(CarbonExtension.NAMESPACE);
    }

    /**
     * Notify server to change the carbons state. This method returns
     * immediately and changes the variable when the reply arrives.
     *
     * You should first check for support using isSupportedByServer().
     *
     * @param new_state whether carbons should be enabled or disabled
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     * @deprecated use {@link #enableCarbonsAsync(ExceptionCallback)} or {@link #disableCarbonsAsync(ExceptionCallback)} instead.
     */
    @Deprecated
    public void sendCarbonsEnabled(final boolean new_state) throws NotConnectedException, InterruptedException {
        sendUseCarbons(new_state, null);
    }

    /**
     * Enable carbons asynchronously. If an error occurs as result of the attempt to enable carbons, the optional
     * <code>exceptionCallback</code> will be invoked.
     * <p>
     * Note that although this method is asynchronous, it may block if the outgoing stream element queue is full (e.g.
     * because of a slow network connection). Thus, if the thread performing this operation is interrupted while the
     * queue is full, an {@link InterruptedException} is thrown.
     * </p>
     *
     * @param exceptionCallback the optional exception callback.
     * @since 4.2
     */
    public void enableCarbonsAsync(ExceptionCallback<Exception> exceptionCallback) {
        sendUseCarbons(true, exceptionCallback);
    }

    /**
     * Disable carbons asynchronously. If an error occurs as result of the attempt to disable carbons, the optional
     * <code>exceptionCallback</code> will be invoked.
     * <p>
     * Note that although this method is asynchronous, it may block if the outgoing stream element queue is full (e.g.
     * because of a slow network connection). Thus, if the thread performing this operation is interrupted while the
     * queue is full, an {@link InterruptedException} is thrown.
     * </p>
     *
     * @param exceptionCallback the optional exception callback.
     * @since 4.2
     */
    public void disableCarbonsAsync(ExceptionCallback<Exception> exceptionCallback) {
        sendUseCarbons(false, exceptionCallback);
    }

    private void sendUseCarbons(final boolean use, ExceptionCallback<Exception> exceptionCallback) {
        IQ setIQ = carbonsEnabledIQ(use);

        SmackFuture<IQ, Exception> future = connection().sendIqRequestAsync(setIQ);

        future.onSuccess(new SuccessCallback<IQ>() {

            @Override
            public void onSuccess(IQ result) {
                enabled_state = use;
            }
        }).onError(exceptionCallback);
    }

    /**
     * Notify server to change the carbons state. This method blocks
     * some time until the server replies to the IQ and returns true on
     * success.
     *
     * You should first check for support using isSupportedByServer().
     *
     * @param new_state whether carbons should be enabled or disabled
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     *
     */
    public synchronized void setCarbonsEnabled(final boolean new_state) throws NoResponseException,
                    XMPPErrorException, NotConnectedException, InterruptedException {
        if (enabled_state == new_state)
            return;

        IQ setIQ = carbonsEnabledIQ(new_state);

        connection().createStanzaCollectorAndSend(setIQ).nextResultOrThrow();
        enabled_state = new_state;
    }

    /**
     * Helper method to enable carbons.
     *
     * @throws XMPPException if an XMPP protocol error was received.
     * @throws SmackException if there was no response from the server.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public void enableCarbons() throws XMPPException, SmackException, InterruptedException {
        setCarbonsEnabled(true);
    }

    /**
     * Helper method to disable carbons.
     *
     * @throws XMPPException if an XMPP protocol error was received.
     * @throws SmackException if there was no response from the server.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public void disableCarbons() throws XMPPException, SmackException, InterruptedException {
        setCarbonsEnabled(false);
    }

    /**
     * Check if carbons are enabled on this connection.
     *
     * @return true if carbons are enabled, else false.
     */
    public boolean getCarbonsEnabled() {
        return this.enabled_state;
    }

    /**
     * Mark a message as "private", so it will not be carbon-copied.
     *
     * @param msg Message object to mark private
     * @deprecated use {@link Private#addTo(Message)}
     */
    @Deprecated
    public static void disableCarbons(Message msg) {
        msg.addExtension(Private.INSTANCE);
    }
}
