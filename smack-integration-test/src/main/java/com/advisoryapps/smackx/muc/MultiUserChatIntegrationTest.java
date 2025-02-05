/**
 *
 * Copyright 2015-2019 Florian Schmaus
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
package com.advisoryapps.smackx.muc;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.advisoryapps.smack.MessageListener;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.packet.Presence;
import com.advisoryapps.smack.util.StringUtils;
import com.advisoryapps.smackx.muc.MultiUserChat.MucCreateConfigFormHandle;
import com.advisoryapps.smackx.muc.MultiUserChatException.MucNotJoinedException;
import com.advisoryapps.smackx.muc.MultiUserChatException.NotAMucServiceException;
import com.advisoryapps.smackx.muc.packet.MUCUser;

import org.igniterealtime.smack.inttest.AbstractSmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTestEnvironment;
import org.igniterealtime.smack.inttest.TestNotPossibleException;
import org.igniterealtime.smack.inttest.util.ResultSyncPoint;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

public class MultiUserChatIntegrationTest extends AbstractSmackIntegrationTest {

    private final String randomString = StringUtils.insecureRandomString(6);

    private final MultiUserChatManager mucManagerOne;
    private final MultiUserChatManager mucManagerTwo;
    private final DomainBareJid mucService;

    public MultiUserChatIntegrationTest(SmackIntegrationTestEnvironment<?> environment)
                    throws NoResponseException, XMPPErrorException, NotConnectedException,
                    InterruptedException, TestNotPossibleException {
        super(environment);
        mucManagerOne = MultiUserChatManager.getInstanceFor(conOne);
        mucManagerTwo = MultiUserChatManager.getInstanceFor(conTwo);

        List<DomainBareJid> services = mucManagerOne.getMucServiceDomains();
        if (services.isEmpty()) {
            throw new TestNotPossibleException("No MUC (XEP-45) service found");
        }
        else {
            mucService = services.get(0);
        }
    }

    @SmackIntegrationTest
    public void mucJoinLeaveTest() throws XmppStringprepException, NotAMucServiceException, NoResponseException,
            XMPPErrorException, NotConnectedException, InterruptedException, MucNotJoinedException {
        EntityBareJid mucAddress = JidCreate.entityBareFrom(Localpart.from("smack-inttest-join-leave-" + randomString),
                mucService.getDomain());

        MultiUserChat muc = mucManagerOne.getMultiUserChat(mucAddress);

        muc.join(Resourcepart.from("nick-one"));

        Presence reflectedLeavePresence = muc.leave();

        MUCUser mucUser = MUCUser.from(reflectedLeavePresence);
        assertNotNull(mucUser);

        assertTrue(mucUser.getStatus().contains(MUCUser.Status.PRESENCE_TO_SELF_110));
    }

    @SmackIntegrationTest
    public void mucTest() throws Exception {
        EntityBareJid mucAddress = JidCreate.entityBareFrom(Localpart.from("smack-inttest-" + randomString), mucService.getDomain());

        MultiUserChat mucAsSeenByOne = mucManagerOne.getMultiUserChat(mucAddress);
        MultiUserChat mucAsSeenByTwo = mucManagerTwo.getMultiUserChat(mucAddress);

        final String mucMessage = "Smack Integration Test MUC Test Message " + randomString;
        final ResultSyncPoint<String, Exception> resultSyncPoint = new ResultSyncPoint<>();

        mucAsSeenByTwo.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(Message message) {
                String body = message.getBody();
                if (mucMessage.equals(body)) {
                    resultSyncPoint.signal(body);
                }
            }
        });

        MucCreateConfigFormHandle handle = mucAsSeenByOne.createOrJoin(Resourcepart.from("one-" + randomString));
        if (handle != null) {
            handle.makeInstant();
        }
        mucAsSeenByTwo.join(Resourcepart.from("two-" + randomString));

        mucAsSeenByOne.sendMessage(mucMessage);
        resultSyncPoint.waitForResult(timeout);

        mucAsSeenByOne.leave();
        mucAsSeenByTwo.leave();
    }
}
