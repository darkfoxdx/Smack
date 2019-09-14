/**
 *
 * Copyright 2017 Paul Schaub
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
package com.advisoryapps.smackx.jingle.transports;

import com.advisoryapps.smack.AbstractConnectionListener;
import com.advisoryapps.smack.XMPPConnection;

import com.advisoryapps.smackx.jingle.JingleSession;
import com.advisoryapps.smackx.jingle.element.JingleContentTransport;

/**
 * Manager for a JingleTransport method.
 * @param <D> JingleContentTransport.
 */
public abstract class JingleTransportManager<D extends JingleContentTransport> extends AbstractConnectionListener {

    private final XMPPConnection connection;

    public JingleTransportManager(XMPPConnection connection) {
        this.connection = connection;
        connection.addConnectionListener(this);
    }

    public XMPPConnection getConnection() {
        return connection;
    }

    public abstract String getNamespace();

    public abstract JingleTransportSession<D> transportSession(JingleSession jingleSession);


    @Override
    public void connected(XMPPConnection connection) {
    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectionClosedOnError(Exception e) {

    }

}
