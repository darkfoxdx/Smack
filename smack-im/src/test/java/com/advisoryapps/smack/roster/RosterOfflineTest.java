/**
 *
 * Copyright the original author or authors
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
package com.advisoryapps.smack.roster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.advisoryapps.smack.DummyConnection;
import com.advisoryapps.smack.SmackException;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the behavior of the roster if the connection is not authenticated yet.
 *
 * @author Henning Staib
 */
public class RosterOfflineTest {

    private DummyConnection connection;

    private Roster roster;

    @Before
    public void setup() {
        this.connection = new DummyConnection();
        assertFalse(connection.isConnected());

        roster = Roster.getInstanceFor(connection);
        assertNotNull(roster);
    }

    @Test(expected = SmackException.class)
    public void shouldThrowExceptionOnReload() throws Exception {
        roster.reload();
    }

    @Test(expected = SmackException.class)
    public void shouldThrowExceptionRemoveEntry() throws Exception {
        roster.removeEntry(null);
    }

}
