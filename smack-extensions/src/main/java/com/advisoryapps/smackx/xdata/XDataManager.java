/**
 *
 * Copyright 2014-2019 Florian Schmaus
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
package com.advisoryapps.smackx.xdata;

import java.util.Map;
import java.util.WeakHashMap;

import com.advisoryapps.smack.ConnectionCreationListener;
import com.advisoryapps.smack.Manager;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPConnectionRegistry;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;

import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;
import com.advisoryapps.smackx.xdata.packet.DataForm;
import com.advisoryapps.smackx.xdata.provider.DescriptionProvider;
import com.advisoryapps.smackx.xdata.provider.FormFieldChildElementProviderManager;
import com.advisoryapps.smackx.xdata.provider.OptionProvider;
import com.advisoryapps.smackx.xdata.provider.RequiredProvider;
import com.advisoryapps.smackx.xdata.provider.ValueProvider;

import org.jxmpp.jid.Jid;

public final class XDataManager extends Manager {

    /**
     * The value of {@link DataForm#NAMESPACE}.
     */
    public static final String NAMESPACE = DataForm.NAMESPACE;

    static {
        FormFieldChildElementProviderManager.addFormFieldChildElementProvider(
                        new DescriptionProvider(),
                        new OptionProvider(),
                        new RequiredProvider(),
                        new ValueProvider()
        );

        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                getInstanceFor(connection);
            }
        });
    }

    private static final Map<XMPPConnection, XDataManager> INSTANCES = new WeakHashMap<>();

    /**
     * Get the XDataManager for the given XMPP connection.
     *
     * @param connection the XMPPConnection.
     * @return the XDataManager
     */
    public static synchronized XDataManager getInstanceFor(XMPPConnection connection) {
        XDataManager xDataManager = INSTANCES.get(connection);
        if (xDataManager == null) {
            xDataManager = new XDataManager(connection);
            INSTANCES.put(connection, xDataManager);
        }
        return xDataManager;
    }

    private XDataManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);
        serviceDiscoveryManager.addFeature(NAMESPACE);
    }

    /**
     * Check if the given entity supports data forms.
     *
     * @param jid the JID of the entity to check.
     * @return true if the entity supports data forms.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     * @see <a href="http://xmpp.org/extensions/xep-0004.html#disco">XEP-0004: Data Forms § 6. Service Discovery</a>
     * @since 4.1
     */
    public boolean isSupported(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(jid, NAMESPACE);
    }
}
