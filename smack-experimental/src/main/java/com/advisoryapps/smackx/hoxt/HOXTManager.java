/**
 *
 * Copyright 2014 Andriy Tsykholyas
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
package com.advisoryapps.smackx.hoxt;

import com.advisoryapps.smack.ConnectionCreationListener;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPConnectionRegistry;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;

import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;
import com.advisoryapps.smackx.hoxt.packet.AbstractHttpOverXmpp;

import org.jxmpp.jid.Jid;

/**
 * Manager for HTTP ove XMPP transport (XEP-0332) extension.
 *
 * @author Andriy Tsykholyas
 * @see <a href="http://xmpp.org/extensions/xep-0332.html">XEP-0332: HTTP over XMPP transport</a>
 */
public class HOXTManager {

    /**
     * Namespace for this extension.
     */
    public static final String NAMESPACE = AbstractHttpOverXmpp.NAMESPACE;

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                ServiceDiscoveryManager.getInstanceFor(connection).addFeature(NAMESPACE);
            }
        });
    }

    /**
     * Returns true if the given entity understands the HTTP ove XMPP transport format and allows the exchange of such.
     *
     * @param jid jid
     * @param connection connection
     * @return true if the given entity understands the HTTP ove XMPP transport format and exchange.
     * @throws XMPPErrorException
     * @throws NoResponseException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public static boolean isSupported(Jid jid, XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection).supportsFeature(jid, NAMESPACE);
    }
}
