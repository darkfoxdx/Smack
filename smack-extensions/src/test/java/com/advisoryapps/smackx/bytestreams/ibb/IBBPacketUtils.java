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
package com.advisoryapps.smackx.bytestreams.ibb;

import com.advisoryapps.smack.packet.EmptyResultIQ;
import com.advisoryapps.smack.packet.ErrorIQ;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.StanzaError;

import org.jxmpp.jid.Jid;

/**
 * Utility methods to create packets.
 *
 * @author Henning Staib
 */
public class IBBPacketUtils {

    /**
     * Returns an error IQ.
     *
     * @param from the senders JID
     * @param to the recipients JID
     * @param condition the XMPP error condition
     * @return an error IQ
     */
    public static IQ createErrorIQ(Jid from, Jid to, StanzaError.Condition condition) {
        StanzaError.Builder xmppError = StanzaError.getBuilder(condition);
        IQ errorIQ = new ErrorIQ(xmppError);
        errorIQ.setType(IQ.Type.error);
        errorIQ.setFrom(from);
        errorIQ.setTo(to);
        return errorIQ;
    }

    /**
     * Returns a result IQ.
     *
     * @param from the senders JID
     * @param to the recipients JID
     * @return a result IQ
     */
    public static IQ createResultIQ(Jid from, Jid to) {
        IQ result = new EmptyResultIQ();
        result.setType(IQ.Type.result);
        result.setFrom(from);
        result.setTo(to);
        return result;
    }

}
