/**
 *
 * Copyright © 2016 Florian Schmaus
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
package com.advisoryapps.smack.filter;

import com.advisoryapps.smack.packet.Stanza;

import org.jxmpp.jid.Jid;

public final class EmptyToMatcher implements StanzaFilter {

    public static final EmptyToMatcher INSTANCE = new EmptyToMatcher();

    private EmptyToMatcher() {
    }

    @Override
    public boolean accept(Stanza packet) {
        Jid packetTo = packet.getTo();
        return packetTo == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
