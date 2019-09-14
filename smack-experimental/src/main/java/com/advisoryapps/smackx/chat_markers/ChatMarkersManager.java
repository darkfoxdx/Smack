/**
 *
 * Copyright © 2016 Fernando Ramirez
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
package com.advisoryapps.smackx.chat_markers;

import java.util.Map;
import java.util.WeakHashMap;

import com.advisoryapps.smack.ConnectionCreationListener;
import com.advisoryapps.smack.Manager;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPConnectionRegistry;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;

import com.advisoryapps.smackx.chat_markers.element.ChatMarkersElements;
import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;

/**
 * Chat Markers Manager class (XEP-0333).
 *
 * @see <a href="http://xmpp.org/extensions/xep-0333.html">XEP-0333: Chat
 *      Markers</a>
 * @author Fernando Ramirez
 *
 */
public final class ChatMarkersManager extends Manager {

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                getInstanceFor(connection);
            }
        });
    }

    private static final Map<XMPPConnection, ChatMarkersManager> INSTANCES = new WeakHashMap<>();

    /**
     * Get the singleton instance of ChatMarkersManager.
     *
     * @param connection
     * @return the instance of ChatMarkersManager
     */
    public static synchronized ChatMarkersManager getInstanceFor(XMPPConnection connection) {
        ChatMarkersManager chatMarkersManager = INSTANCES.get(connection);

        if (chatMarkersManager == null) {
            chatMarkersManager = new ChatMarkersManager(connection);
            INSTANCES.put(connection, chatMarkersManager);
        }

        return chatMarkersManager;
    }

    private ChatMarkersManager(XMPPConnection connection) {
        super(connection);
    }

    /**
     * Returns true if Chat Markers is supported by the server.
     *
     * @return true if Chat Markers is supported by the server.
     * @throws NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     * @throws InterruptedException
     */
    public boolean isSupportedByServer()
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection())
                .serverSupportsFeature(ChatMarkersElements.NAMESPACE);
    }

}
