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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.advisoryapps.smack.AbstractXMPPConnection;
import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.XMPPException;
import com.advisoryapps.smack.util.StringUtils;

import com.advisoryapps.smackx.bookmarks.BookmarkManager;
import com.advisoryapps.smackx.muc.MultiUserChat.MucCreateConfigFormHandle;
import com.advisoryapps.smackx.muc.bookmarkautojoin.MucBookmarkAutojoinManager;

import org.igniterealtime.smack.inttest.AbstractSmackLowLevelIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTestEnvironment;
import org.igniterealtime.smack.inttest.TestNotPossibleException;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;

public class MultiUserChatLowLevelIntegrationTest extends AbstractSmackLowLevelIntegrationTest {

    public MultiUserChatLowLevelIntegrationTest(SmackIntegrationTestEnvironment<?> environment) throws Exception {
        super(environment);
        AbstractXMPPConnection connection = getConnectedConnection();
        try {
            if (MultiUserChatManager.getInstanceFor(connection).getMucServiceDomains().isEmpty()) {
                throw new TestNotPossibleException("MUC component not offered by service");
            }
        } finally {
            recycle(connection);
        }
    }

    @SmackIntegrationTest
    public void testMucBookmarksAutojoin(AbstractXMPPConnection connection) throws InterruptedException,
                    TestNotPossibleException, XMPPException, SmackException, IOException {
        final BookmarkManager bookmarkManager = BookmarkManager.getBookmarkManager(connection);
        if (!bookmarkManager.isSupported()) {
            throw new TestNotPossibleException("Private data storage not supported");
        }
        final MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        final Resourcepart mucNickname = Resourcepart.from("Nick-" + StringUtils.randomString(6));
        final String randomMucName = StringUtils.randomString(6);
        final DomainBareJid mucComponent = multiUserChatManager.getMucServiceDomains().get(0);
        final MultiUserChat muc = multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom(
                        Localpart.from(randomMucName), mucComponent));

        MucCreateConfigFormHandle handle = muc.createOrJoin(mucNickname);
        if (handle != null) {
            handle.makeInstant();
        }
        muc.leave();

        bookmarkManager.addBookmarkedConference("Smack Inttest: " + testRunId, muc.getRoom(), true,
                        mucNickname, null);

        connection.disconnect();
        connection.connect().login();

        // MucBookmarkAutojoinManager is also able to do its task automatically
        // after every login, it's not deterministic when this will be finished.
        // So we trigger it manually here.
        MucBookmarkAutojoinManager.getInstanceFor(connection).autojoinBookmarkedConferences();

       assertTrue(muc.isJoined());

       // If the test went well, leave the MUC
       muc.leave();
    }

}
