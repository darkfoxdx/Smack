/**
 *
 * Copyright 2003 Jive Software.
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

package com.advisoryapps.smack.packet;

/**
 * A mock implementation of the Stanza abstract class. Implements toXML() by returning null.
 */
public class MockPacket extends Packet {

    /**
     * Returns null always.
     * @return null TODO javadoc me please
     */
    public String toXML() {
        return null;
    }
}
