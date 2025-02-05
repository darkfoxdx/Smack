/**
 *
 * Copyright 2014-2018 Florian Schmaus
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
package com.advisoryapps.smack;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import com.advisoryapps.smack.SmackException.NotLoggedInException;
import com.advisoryapps.smack.util.Objects;

/**
 * Managers provide the high-level API of certain functionality (often standardized by XMPP Extension Protocols).
 */
public abstract class Manager {

    final WeakReference<XMPPConnection> weakConnection;

    public Manager(XMPPConnection connection) {
        Objects.requireNonNull(connection, "XMPPConnection must not be null");

        weakConnection = new WeakReference<>(connection);
    }

    protected final XMPPConnection connection() {
        return weakConnection.get();
    }

    /**
     * Get the XMPPConnection of this Manager if it's authenticated, i.e. logged in. Otherwise throw a {@link NotLoggedInException}.
     *
     * @return the XMPPConnection of this Manager.
     * @throws NotLoggedInException if the connection is not authenticated.
     */
    protected final XMPPConnection getAuthenticatedConnectionOrThrow() throws NotLoggedInException {
        XMPPConnection connection = connection();
        if (!connection.isAuthenticated()) {
            throw new NotLoggedInException();
        }
        return connection;
    }

    protected static final ScheduledAction schedule(Runnable runnable, long delay, TimeUnit unit) {
        return AbstractXMPPConnection.SMACK_REACTOR.schedule(runnable, delay, unit);
    }
}
