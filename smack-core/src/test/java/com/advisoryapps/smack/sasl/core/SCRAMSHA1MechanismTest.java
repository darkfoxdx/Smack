/**
 *
 * Copyright 2014-2016 Florian Schmaus
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
package com.advisoryapps.smack.sasl.core;

import static org.junit.Assert.assertEquals;

import com.advisoryapps.smack.DummyConnection;
import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.sasl.packet.SaslStreamElements.AuthMechanism;
import com.advisoryapps.smack.sasl.packet.SaslStreamElements.Response;
import com.advisoryapps.smack.test.util.SmackTestSuite;
import com.advisoryapps.smack.util.stringencoder.Base64;

import org.junit.Test;
import org.jxmpp.jid.JidTestUtil;

public class SCRAMSHA1MechanismTest extends SmackTestSuite {

    public static final String USERNAME = "user";
    public static final String PASSWORD = "pencil";
    public static final String CLIENT_FIRST_MESSAGE = "n,,n=user,r=fyko+d2lbbFgONRv9qkxdawL";
    public static final String SERVER_FIRST_MESSAGE = "r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,s=QSXCR+Q6sek8bf92,i=4096";
    public static final String CLIENT_FINAL_MESSAGE = "c=biws,r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,p=v0X8v3Bz2T0CJGbJQyF0X+HI4Ts=";
    public static final String SERVER_FINAL_MESSAGE = "v=rmF9pqV8S7suAoZWja4dJRkFsKQ=";

    @Test
    public void testScramSha1Mechanism() throws NotConnectedException, SmackException, InterruptedException {
        final DummyConnection con = new DummyConnection();
        SCRAMSHA1Mechanism mech = new SCRAMSHA1Mechanism() {
            @Override
            public String getRandomAscii() {
                this.connection = con;
                return "fyko+d2lbbFgONRv9qkxdawL";
            }
        };

        mech.authenticate(USERNAME, "unusedFoo", JidTestUtil.DOMAIN_BARE_JID_1, PASSWORD, null, null);
        AuthMechanism authMechanism = con.getSentPacket();
        assertEquals(SCRAMSHA1Mechanism.NAME, authMechanism.getMechanism());
        assertEquals(CLIENT_FIRST_MESSAGE, saslLayerString(authMechanism.getAuthenticationText()));

        mech.challengeReceived(Base64.encode(SERVER_FIRST_MESSAGE), false);
        Response response = con.getSentPacket();
        assertEquals(CLIENT_FINAL_MESSAGE, saslLayerString(response.getAuthenticationText()));

        mech.challengeReceived(Base64.encode(SERVER_FINAL_MESSAGE), true);
        mech.checkIfSuccessfulOrThrow();
    }

    private static String saslLayerString(String string) {
        return Base64.decodeToString(string);
    }
}
