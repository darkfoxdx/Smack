/**
 *
 * Copyright 2018 Paul Schaub
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
package com.advisoryapps.smackx.reference;

import java.util.Map;
import java.util.WeakHashMap;

import com.advisoryapps.smack.ConnectionCreationListener;
import com.advisoryapps.smack.Manager;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPConnectionRegistry;

import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;

public final class ReferenceManager extends Manager {

    public static final String NAMESPACE = "urn:xmpp:reference:0";

    private static final Map<XMPPConnection, ReferenceManager> INSTANCES = new WeakHashMap<>();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                getInstanceFor(connection);
            }
        });
    }

    private ReferenceManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(NAMESPACE);
    }

    /**
     * Return a new instance of the ReferenceManager for the given connection.
     *
     * @param connection xmpp connection
     * @return reference manager instance
     */
    public static synchronized ReferenceManager getInstanceFor(XMPPConnection connection) {
        ReferenceManager manager = INSTANCES.get(connection);
        if (manager == null) {
            manager = new ReferenceManager(connection);
            INSTANCES.put(connection, manager);
        }
        return manager;
    }
}
