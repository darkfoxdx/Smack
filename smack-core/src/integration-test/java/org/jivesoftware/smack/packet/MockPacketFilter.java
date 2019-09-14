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

package com.advisoryapps.smack.packet;

import com.advisoryapps.smack.filter.PacketFilter;

/**
 * A mock implementation of the PacketFilter class. Pass in the value you want the
 * accept(..) method to return.
 */
public class MockPacketFilter implements PacketFilter {

    private boolean acceptValue;

    public MockPacketFilter(boolean acceptValue) {
        this.acceptValue = acceptValue;
    }

    public boolean accept(Packet packet) {
        return acceptValue;
    }

}
