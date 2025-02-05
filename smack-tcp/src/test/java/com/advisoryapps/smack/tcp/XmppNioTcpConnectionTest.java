/**
 *
 * Copyright 2018 Florian Schmaus
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
package com.advisoryapps.smack.tcp;

import com.advisoryapps.smack.fsm.StateDescriptor;
import com.advisoryapps.smack.fsm.StateDescriptorGraph.GraphVertex;
import com.advisoryapps.smack.tcp.XmppNioTcpConnection.InstantShutdownStateDescriptor;

public class XmppNioTcpConnectionTest {

    public void graphComplete() {
        assertContains(XmppNioTcpConnection.INITIAL_STATE_DESCRIPTOR_VERTEX, InstantShutdownStateDescriptor.class);
    }

    private static void assertContains(GraphVertex<StateDescriptor> graph, Class<? extends StateDescriptor> state) {
        // TODO: Implement this.
        throw new Error("Implement me: " + graph + " " + state);
    }
}
