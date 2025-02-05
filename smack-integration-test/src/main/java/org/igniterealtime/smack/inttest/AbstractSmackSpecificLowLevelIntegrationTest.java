/**
 *
 * Copyright 2018-2019 Florian Schmaus
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
package org.igniterealtime.smack.inttest;

import java.util.ArrayList;
import java.util.List;

import com.advisoryapps.smack.AbstractXMPPConnection;
import com.advisoryapps.smack.ConnectionConfiguration;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;

public abstract class AbstractSmackSpecificLowLevelIntegrationTest<C extends AbstractXMPPConnection>
        extends AbstractSmackLowLevelIntegrationTest {

    private final SmackIntegrationTestEnvironment<?> environment;

    protected final Class<C> connectionClass;

    private final XmppConnectionDescriptor<C, ? extends ConnectionConfiguration, ? extends ConnectionConfiguration.Builder<?, ?>> connectionDescriptor;

    public AbstractSmackSpecificLowLevelIntegrationTest(SmackIntegrationTestEnvironment<?> environment,
            Class<C> connectionClass) {
        super(environment);
        this.environment = environment;
        this.connectionClass = connectionClass;

        connectionDescriptor = environment.connectionManager.getConnectionDescriptorFor(connectionClass);
    }

    public Class<C> getConnectionClass() {
        return connectionClass;
    }

    protected C getSpecificUnconnectedConnection() throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return environment.connectionManager.constructConnection(connectionDescriptor);
    }

    protected List<C> getSpecificUnconnectedConnections(int count)
                    throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        List<C> connections = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            C connection = getSpecificUnconnectedConnection();
            connections.add(connection);
        }
        return connections;
    }
}
