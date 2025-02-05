/**
 *
 * Copyright © 2014 Florian Schmaus
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
package com.advisoryapps.smackx.csi;

import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;

import com.advisoryapps.smackx.csi.packet.ClientStateIndication;

/**
 * Manager for Client State Indication.
 *
 * @see <a href="http://xmpp.org/extensions/xep-0352.html">XEP-0352: Client State Indication</a>
 *
 */
public class ClientStateIndicationManager {

    public static void active(XMPPConnection connection) throws NotConnectedException, InterruptedException {
        throwIaeIfNotSupported(connection);
        connection.sendNonza(ClientStateIndication.Active.INSTANCE);
    }

    public static void inactive(XMPPConnection connection) throws NotConnectedException, InterruptedException {
        throwIaeIfNotSupported(connection);
        connection.sendNonza(ClientStateIndication.Inactive.INSTANCE);
    }

    public static boolean isSupported(XMPPConnection connection) {
        return connection.hasFeature(ClientStateIndication.Feature.ELEMENT, ClientStateIndication.NAMESPACE);
    }

    private static void throwIaeIfNotSupported(XMPPConnection connection) {
        if (!isSupported(connection)) {
            throw new IllegalArgumentException("Client State Indication not supported by server");
        }
    }
}
