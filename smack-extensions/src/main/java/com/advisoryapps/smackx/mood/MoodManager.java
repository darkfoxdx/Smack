/**
 *
 * Copyright 2018 Paul Schaub.
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
package com.advisoryapps.smackx.mood;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.advisoryapps.smack.AsyncButOrdered;
import com.advisoryapps.smack.Manager;
import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.provider.ProviderManager;

import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;
import com.advisoryapps.smackx.mood.element.MoodConcretisation;
import com.advisoryapps.smackx.mood.element.MoodElement;
import com.advisoryapps.smackx.mood.provider.MoodConcretisationProvider;
import com.advisoryapps.smackx.pep.PepListener;
import com.advisoryapps.smackx.pep.PepManager;
import com.advisoryapps.smackx.pubsub.EventElement;
import com.advisoryapps.smackx.pubsub.ItemsExtension;
import com.advisoryapps.smackx.pubsub.LeafNode;
import com.advisoryapps.smackx.pubsub.PayloadItem;
import com.advisoryapps.smackx.pubsub.PubSubException;
import com.advisoryapps.smackx.pubsub.PubSubManager;

import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;

/**
 * Entry point for Smacks API for XEP-0107: User Mood.
 *
 * To set a mood, please use one of the {@link #setMood(Mood)} methods. This will publish the users mood to a pubsub
 * node.<br>
 * <br>
 * In order to get updated about other users moods, register a {@link MoodListener} at
 * {@link #addMoodListener(MoodListener)}. That listener will get updated about any incoming mood updates of contacts.<br>
 * <br>
 * To stop publishing the users mood, refer to {@link #clearMood()}.<br>
 * <br>
 * It is also possible to add {@link MoodElement}s to {@link Message}s by using {@link #addMoodToMessage(Message, Mood)}.<br>
 * <br>
 * The API can be extended with custom mood concretisations by extending {@link MoodConcretisation} and registering
 * {@link MoodConcretisationProvider}s using {@link ProviderManager#addExtensionProvider(String, String, Object)}.<br>
 * An example of how this can be done can be found in the MoodConcretisationTest in the test package.
 *
 * @see <a href="https://xmpp.org/extensions/xep-0107.html">
 *     XEP-0107: User Mood</a>
 */
public final class MoodManager extends Manager {

    public static final String MOOD_NODE = "http://jabber.org/protocol/mood";
    public static final String MOOD_NOTIFY = MOOD_NODE + "+notify";

    private static final Map<XMPPConnection, MoodManager> INSTANCES = new WeakHashMap<>();

    private final Set<MoodListener> moodListeners = new HashSet<>();
    private final AsyncButOrdered<BareJid> asyncButOrdered = new AsyncButOrdered<>();
    private PubSubManager pubSubManager;

    private MoodManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(MOOD_NOTIFY);
        PepManager.getInstanceFor(connection).addPepListener(new PepListener() {
            @Override
            public void eventReceived(final EntityBareJid from, final EventElement event, final Message message) {
                if (!MOOD_NODE.equals(event.getEvent().getNode())) {
                    return;
                }

                final BareJid contact = from.asBareJid();
                asyncButOrdered.performAsyncButOrdered(contact, new Runnable() {
                    @Override
                    public void run() {
                        ItemsExtension items = (ItemsExtension) event.getExtensions().get(0);
                        PayloadItem<?> payload = (PayloadItem) items.getItems().get(0);
                        MoodElement mood = (MoodElement) payload.getPayload();

                        for (MoodListener listener : moodListeners) {
                            listener.onMoodUpdated(contact, message, mood);
                        }
                    }
                });
            }
        });
    }

    public static synchronized MoodManager getInstanceFor(XMPPConnection connection) {
        MoodManager manager = INSTANCES.get(connection);
        if (manager == null) {
            manager = new MoodManager(connection);
            INSTANCES.put(connection, manager);
        }
        return manager;
    }

    public void setMood(Mood mood)
            throws InterruptedException, SmackException.NotLoggedInException, SmackException.NoResponseException,
            SmackException.NotConnectedException, XMPPException.XMPPErrorException, PubSubException.NotALeafNodeException {
        setMood(mood, null, null);
    }

    public void setMood(Mood mood, String text)
            throws InterruptedException, SmackException.NotLoggedInException, SmackException.NoResponseException,
            SmackException.NotConnectedException, XMPPException.XMPPErrorException, PubSubException.NotALeafNodeException {
        setMood(mood, null, text);
    }

    public void setMood(Mood mood, MoodConcretisation concretisation)
            throws InterruptedException, SmackException.NotLoggedInException, SmackException.NoResponseException,
            SmackException.NotConnectedException, XMPPException.XMPPErrorException, PubSubException.NotALeafNodeException {
        setMood(mood, concretisation, null);
    }

    public void setMood(Mood mood, MoodConcretisation concretisation, String text)
            throws InterruptedException, SmackException.NotLoggedInException, SmackException.NoResponseException,
            SmackException.NotConnectedException, XMPPException.XMPPErrorException, PubSubException.NotALeafNodeException {
        MoodElement element = buildMood(mood, concretisation, text);
        publishMood(element);
    }

    public void clearMood()
            throws InterruptedException, SmackException.NotLoggedInException, SmackException.NoResponseException,
            SmackException.NotConnectedException, XMPPException.XMPPErrorException, PubSubException.NotALeafNodeException {
        MoodElement element = buildMood(null, null, null);
        publishMood(element);
    }

    private void publishMood(MoodElement moodElement)
            throws SmackException.NotLoggedInException, InterruptedException, PubSubException.NotALeafNodeException,
            XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        if (pubSubManager == null) {
            pubSubManager = PubSubManager.getInstanceFor(getAuthenticatedConnectionOrThrow(), connection().getUser().asBareJid());
        }

        LeafNode node = pubSubManager.getOrCreateLeafNode(MOOD_NODE);
        node.publish(new PayloadItem<>(moodElement));
    }

    private static MoodElement buildMood(Mood mood, MoodConcretisation concretisation, String text) {
        return new MoodElement(
                new MoodElement.MoodSubjectElement(mood, concretisation),
                text);
    }

    public static void addMoodToMessage(Message message, Mood mood) {
        addMoodToMessage(message, mood, null);
    }

    public static void addMoodToMessage(Message message, Mood mood, MoodConcretisation concretisation) {
        MoodElement element = buildMood(mood, concretisation, null);
        message.addExtension(element);
    }

    public synchronized void addMoodListener(MoodListener listener) {
        moodListeners.add(listener);
    }

    public synchronized void removeMoodListener(MoodListener listener) {
        moodListeners.remove(listener);
    }
}
