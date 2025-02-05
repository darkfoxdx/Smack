/**
 *
 * Copyright 2017 Florian Schmaus
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
package com.advisoryapps.smackx.pubsub;

import com.advisoryapps.smack.SmackException;

import com.advisoryapps.smackx.disco.packet.DiscoverInfo;

import org.jxmpp.jid.BareJid;

public abstract class PubSubException extends SmackException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final String nodeId;

    protected PubSubException(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public static class NotALeafNodeException extends PubSubException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final BareJid pubSubService;

        NotALeafNodeException(String nodeId, BareJid pubSubService) {
            super(nodeId);
            this.pubSubService = pubSubService;
        }

        public BareJid getPubSubService() {
            return pubSubService;
        }

    }

    public static class NotAPubSubNodeException extends PubSubException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final DiscoverInfo discoverInfo;

        NotAPubSubNodeException(String nodeId, DiscoverInfo discoverInfo) {
            super(nodeId);
            this.discoverInfo = discoverInfo;
        }

        public DiscoverInfo getDiscoverInfo() {
            return discoverInfo;
        }
    }
}
