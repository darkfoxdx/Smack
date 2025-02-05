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

package com.advisoryapps.smackx.disco;

import java.util.List;

import com.advisoryapps.smack.packet.ExtensionElement;

import com.advisoryapps.smackx.disco.packet.DiscoverInfo;
import com.advisoryapps.smackx.disco.packet.DiscoverItems;


/**
 * The NodeInformationProvider is responsible for providing supported identities, features
 * and hosted items (i.e. DiscoverItems.Item) about a given node. This information will be
 * requested each time this XMPP client receives a disco info or items requests on the
 * given node.
 *
 * @author Gaston Dombiak
 */
public interface NodeInformationProvider {

    /**
     * Returns a list of the Items {@link com.advisoryapps.smackx.disco.packet.DiscoverItems.Item}
     * defined in the node. For example, the MUC protocol specifies that an XMPP client should
     * answer an Item for each joined room when asked for the rooms where the use has joined.
     *
     * @return a list of the Items defined in the node.
     */
    List<DiscoverItems.Item> getNodeItems();

    /**
     * Returns a list of the features defined in the node. For
     * example, the entity caps protocol specifies that an XMPP client
     * should answer with each feature supported by the client version
     * or extension.
     *
     * @return a list of the feature strings defined in the node.
     */
    List<String> getNodeFeatures();

    /**
     * Returns a list of the identities defined in the node. For
     * example, the x-command protocol must provide an identity of
     * category automation and type command-node for each command.
     *
     * @return a list of the Identities defined in the node.
     */
    List<DiscoverInfo.Identity> getNodeIdentities();

    /**
     * Returns a list of the stanza extensions defined in the node.
     *
     * @return a list of the stanza extensions defined in the node.
     */
    List<ExtensionElement> getNodePacketExtensions();
}
