/**
 *
 * Copyright 2003-2007 Jive Software.
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

package com.advisoryapps.smackx.xhtmlim;

import java.util.List;

import com.advisoryapps.smack.ConnectionCreationListener;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPConnectionRegistry;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;
import com.advisoryapps.smack.packet.Message;

import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;
import com.advisoryapps.smackx.xhtmlim.packet.XHTMLExtension;

import org.jxmpp.jid.Jid;

/**
 * Manages XHTML formatted texts within messages. A XHTMLManager provides a high level access to
 * get and set XHTML bodies to messages, enable and disable XHTML support and check if remote XMPP
 * clients support XHTML.
 *
 * @author Gaston Dombiak
 */
public class XHTMLManager {
    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                // Enable the XHTML support on every established connection
                XHTMLManager.setServiceEnabled(connection, true);
            }
        });
    }

    /**
     * Returns an Iterator for the XHTML bodies in the message. Returns null if
     * the message does not contain an XHTML extension.
     *
     * @param message an XHTML message
     * @return an Iterator for the bodies in the message or null if none.
     */
    public static List<CharSequence> getBodies(Message message) {
        XHTMLExtension xhtmlExtension = XHTMLExtension.from(message);
        if (xhtmlExtension != null)
            return xhtmlExtension.getBodies();
        else
            return null;
    }

    /**
     * Adds an XHTML body to the message.
     *
     * @param message the message that will receive the XHTML body
     * @param xhtmlText the string to add as an XHTML body to the message
     */
    public static void addBody(Message message, XHTMLText xhtmlText) {
        XHTMLExtension xhtmlExtension = XHTMLExtension.from(message);
        if (xhtmlExtension == null) {
            // Create an XHTMLExtension and add it to the message
            xhtmlExtension = new XHTMLExtension();
            message.addExtension(xhtmlExtension);
        }
        // Add the required bodies to the message
        xhtmlExtension.addBody(xhtmlText.toXML());
    }

    /**
     * Returns true if the message contains an XHTML extension.
     *
     * @param message the message to check if contains an XHTML extension or not
     * @return a boolean indicating whether the message is an XHTML message
     */
    public static boolean isXHTMLMessage(Message message) {
        return message.getExtension(XHTMLExtension.ELEMENT, XHTMLExtension.NAMESPACE) != null;
    }

    /**
     * Enables or disables the XHTML support on a given connection.<p>
     *
     * Before starting to send XHTML messages to a user, check that the user can handle XHTML
     * messages. Enable the XHTML support to indicate that this client handles XHTML messages.
     *
     * @param connection the connection where the service will be enabled or disabled
     * @param enabled indicates if the service will be enabled or disabled
     */
    public static synchronized void setServiceEnabled(XMPPConnection connection, boolean enabled) {
        if (isServiceEnabled(connection) == enabled)
            return;

        if (enabled) {
            ServiceDiscoveryManager.getInstanceFor(connection).addFeature(XHTMLExtension.NAMESPACE);
        }
        else {
            ServiceDiscoveryManager.getInstanceFor(connection).removeFeature(XHTMLExtension.NAMESPACE);
        }
    }

    /**
     * Returns true if the XHTML support is enabled for the given connection.
     *
     * @param connection the connection to look for XHTML support
     * @return a boolean indicating if the XHTML support is enabled for the given connection
     */
    public static boolean isServiceEnabled(XMPPConnection connection) {
        return ServiceDiscoveryManager.getInstanceFor(connection).includesFeature(XHTMLExtension.NAMESPACE);
    }

    /**
     * Returns true if the specified user handles XHTML messages.
     *
     * @param connection the connection to use to perform the service discovery
     * @param userID the user to check. A fully qualified xmpp ID, e.g. jdoe@example.com
     * @return a boolean indicating whether the specified user handles XHTML messages
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public static boolean isServiceEnabled(XMPPConnection connection, Jid userID)
                    throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection).supportsFeature(userID, XHTMLExtension.NAMESPACE);
    }
}
