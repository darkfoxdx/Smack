/**
 *
 * Copyright 2014 Florian Schmaus
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
package com.advisoryapps.smackx.time;

import java.util.Map;
import java.util.WeakHashMap;

import com.advisoryapps.smack.ConnectionCreationListener;
import com.advisoryapps.smack.Manager;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPConnectionRegistry;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;
import com.advisoryapps.smack.iqrequest.AbstractIqRequestHandler;
import com.advisoryapps.smack.iqrequest.IQRequestHandler.Mode;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.IQ.Type;
import com.advisoryapps.smack.packet.StanzaError.Condition;

import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;
import com.advisoryapps.smackx.time.packet.Time;

import org.jxmpp.jid.Jid;

public final class EntityTimeManager extends Manager {

    private static final Map<XMPPConnection, EntityTimeManager> INSTANCES = new WeakHashMap<>();

    private static boolean autoEnable = true;

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                getInstanceFor(connection);
            }
        });
    }

    public static void setAutoEnable(boolean autoEnable) {
        EntityTimeManager.autoEnable = autoEnable;
    }

    public static synchronized EntityTimeManager getInstanceFor(XMPPConnection connection) {
        EntityTimeManager entityTimeManager = INSTANCES.get(connection);
        if (entityTimeManager == null) {
            entityTimeManager = new EntityTimeManager(connection);
            INSTANCES.put(connection, entityTimeManager);
        }
        return entityTimeManager;
    }

    private boolean enabled = false;

    private EntityTimeManager(XMPPConnection connection) {
        super(connection);
        if (autoEnable)
            enable();

        connection.registerIQRequestHandler(new AbstractIqRequestHandler(Time.ELEMENT, Time.NAMESPACE, Type.get,
                        Mode.async) {
            @Override
            public IQ handleIQRequest(IQ iqRequest) {
                if (enabled) {
                    return Time.createResponse(iqRequest);
                }
                else {
                    return IQ.createErrorResponse(iqRequest, Condition.not_acceptable);
                }
            }
        });
    }

    public synchronized void enable() {
        if (enabled)
            return;
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection());
        sdm.addFeature(Time.NAMESPACE);
        enabled = true;
    }

    public synchronized void disable() {
        if (!enabled)
            return;
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection());
        sdm.removeFeature(Time.NAMESPACE);
        enabled = false;
    }

    public boolean isTimeSupported(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException  {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(jid, Time.NAMESPACE);
    }

    public Time getTime(Jid jid) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (!isTimeSupported(jid))
            return null;

        Time request = new Time();
        // TODO Add Time(Jid) constructor and use this constructor instead
        request.setTo(jid);
        return connection().createStanzaCollectorAndSend(request).nextResultOrThrow();
    }
}
