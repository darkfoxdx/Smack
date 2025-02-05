/**
 *
 * Copyright © 2014-2019 Florian Schmaus
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
package com.advisoryapps.smack.sasl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.util.StringUtils;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

public class DigestMd5SaslTest extends AbstractSaslTest {

    protected static final String challenge = "realm=\"xmpp.org\",nonce=\"jgGgnz+cQcmyVaAs2n88kQ==\",qop=\"auth\",charset=utf-8,algorithm=md5-sess";
    protected static final byte[] challengeBytes = StringUtils.toUtf8Bytes(challenge);

    public DigestMd5SaslTest(SASLMechanism saslMechanism) {
        super(saslMechanism);
    }

    protected void runTest(boolean useAuthzid) throws SmackException, InterruptedException, XmppStringprepException {
        EntityBareJid authzid = null;
        if (useAuthzid) {
            authzid = JidCreate.entityBareFrom("shazbat@xmpp.org");
        }
        saslMechanism.authenticate("florian", "irrelevant", JidCreate.domainBareFrom("xmpp.org"), "secret", authzid, null);
        byte[] response = saslMechanism.evaluateChallenge(challengeBytes);
        String responseString = new String(response, StandardCharsets.UTF_8);
        String[] responseParts = responseString.split(",");
        Map<String, String> responsePairs = new HashMap<String, String>();
        for (String part : responseParts) {
            String[] keyValue = part.split("=", 2);
            String key = keyValue[0];
            String value = keyValue[1].replace("\"", "");
            responsePairs.put(key, value);
        }
        if (useAuthzid) {
          assertMapValue("authzid", "shazbat@xmpp.org", responsePairs);
        } else {
          assertTrue (!responsePairs.containsKey("authzid"));
        }
        assertMapValue("username", "florian", responsePairs);
        assertMapValue("realm", "xmpp.org", responsePairs);
        assertMapValue("digest-uri", "xmpp/xmpp.org", responsePairs);
        assertMapValue("qop", "auth", responsePairs);
    }

    private static void assertMapValue(String key, String value, Map<String, String> map) {
        assertEquals(value, map.get(key));
    }
}
