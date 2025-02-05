/**
 *
 * Copyright 2014 Vyacheslav Blinov
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
package com.advisoryapps.smackx.amp;

import com.advisoryapps.smack.ConnectionCreationListener;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPConnectionRegistry;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;

import com.advisoryapps.smackx.amp.packet.AMPExtension;
import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;

/**
 * Manages AMP stanzas within messages. A AMPManager provides a high level access to
 * get and set AMP rules to messages.
 *
 * See http://xmpp.org/extensions/xep-0079.html for AMP extension details
 *
 * @author Vyacheslav Blinov
 */
public class AMPManager {


    // Enable the AMP support on every established connection
    // The ServiceDiscoveryManager class should have been already initialized
    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                AMPManager.setServiceEnabled(connection, true);
            }
        });
    }

    /**
     * Enables or disables the AMP support on a given connection.<p>
     *
     * Before starting to send AMP messages to a user, check that the user can handle XHTML
     * messages. Enable the AMP support to indicate that this client handles XHTML messages.
     *
     * @param connection the connection where the service will be enabled or disabled
     * @param enabled indicates if the service will be enabled or disabled
     */
    public static synchronized void setServiceEnabled(XMPPConnection connection, boolean enabled) {
        if (isServiceEnabled(connection) == enabled)
            return;

        if (enabled) {
            ServiceDiscoveryManager.getInstanceFor(connection).addFeature(AMPExtension.NAMESPACE);
        }
        else {
            ServiceDiscoveryManager.getInstanceFor(connection).removeFeature(AMPExtension.NAMESPACE);
        }
    }

    /**
     * Returns true if the AMP support is enabled for the given connection.
     *
     * @param connection the connection to look for AMP support
     * @return a boolean indicating if the AMP support is enabled for the given connection
     */
    public static boolean isServiceEnabled(XMPPConnection connection) {
        connection.getXMPPServiceDomain();
        return ServiceDiscoveryManager.getInstanceFor(connection).includesFeature(AMPExtension.NAMESPACE);
    }

    /**
     * Check if server supports specified action.
     * @param connection active xmpp connection
     * @param action action to check
     * @return true if this action is supported.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public static boolean isActionSupported(XMPPConnection connection, AMPExtension.Action action) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        String featureName = AMPExtension.NAMESPACE + "?action=" + action.toString();
        return isFeatureSupportedByServer(connection, featureName);
    }

    /**
     * Check if server supports specified condition.
     * @param connection active xmpp connection
     * @param conditionName name of condition to check
     * @return true if this condition is supported.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     * @see AMPDeliverCondition
     * @see AMPExpireAtCondition
     * @see AMPMatchResourceCondition
     */
    public static boolean isConditionSupported(XMPPConnection connection, String conditionName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        String featureName = AMPExtension.NAMESPACE + "?condition=" + conditionName;
        return isFeatureSupportedByServer(connection, featureName);
    }

    private static boolean isFeatureSupportedByServer(XMPPConnection connection, String featureName) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection).serverSupportsFeature(featureName);
    }
}
