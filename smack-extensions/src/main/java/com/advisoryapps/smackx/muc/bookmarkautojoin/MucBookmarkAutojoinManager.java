/**
 *
 * Copyright © 2015 Florian Schmaus
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
package com.advisoryapps.smackx.muc.bookmarkautojoin;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.advisoryapps.smack.AbstractConnectionListener;
import com.advisoryapps.smack.ConnectionCreationListener;
import com.advisoryapps.smack.Manager;
import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPConnectionRegistry;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;

import com.advisoryapps.smackx.bookmarks.BookmarkManager;
import com.advisoryapps.smackx.bookmarks.BookmarkedConference;
import com.advisoryapps.smackx.muc.MultiUserChat;
import com.advisoryapps.smackx.muc.MultiUserChat.MucCreateConfigFormHandle;
import com.advisoryapps.smackx.muc.MultiUserChatException.NotAMucServiceException;
import com.advisoryapps.smackx.muc.MultiUserChatManager;

import org.jxmpp.jid.parts.Resourcepart;

/**
 * Autojoin bookmarked Multi-User Chat conferences.
 *
 * @see <a href="http://xmpp.org/extensions/xep-0048.html">XEP-48: Bookmarks</a>
 *
 */
public final class MucBookmarkAutojoinManager extends Manager {

    private static final Logger LOGGER = Logger.getLogger(MucBookmarkAutojoinManager.class.getName());

    private static final Map<XMPPConnection, MucBookmarkAutojoinManager> INSTANCES = new WeakHashMap<>();

    private static boolean autojoinEnabledDefault = false;

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                getInstanceFor(connection);
            }
        });
    }

    public static void setAutojoinPerDefault(boolean autojoin) {
        autojoinEnabledDefault = autojoin;
    }

    public static synchronized MucBookmarkAutojoinManager getInstanceFor(XMPPConnection connection) {
        MucBookmarkAutojoinManager mbam = INSTANCES.get(connection);
        if (mbam == null) {
            mbam = new MucBookmarkAutojoinManager(connection);
            INSTANCES.put(connection, mbam);
        }
        return mbam;
    }

    private final MultiUserChatManager multiUserChatManager;
    private final BookmarkManager bookmarkManager;

    private boolean autojoinEnabled = autojoinEnabledDefault;

    private MucBookmarkAutojoinManager(XMPPConnection connection) {
        super(connection);
        multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        bookmarkManager = BookmarkManager.getBookmarkManager(connection);
        connection.addConnectionListener(new AbstractConnectionListener() {
            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                if (!autojoinEnabled) {
                    return;
                }
                // TODO handle resumed case?
                autojoinBookmarkedConferences();
            }
        });
    }

    public void setAutojoinEnabled(boolean autojoin) {
        autojoinEnabled = autojoin;
    }

    public void autojoinBookmarkedConferences() {
        List<BookmarkedConference> bookmarkedConferences;
        try {
            bookmarkedConferences = bookmarkManager.getBookmarkedConferences();
        }
        catch (NotConnectedException | InterruptedException e) {
            LOGGER.log(Level.FINER, "Could not get MUC bookmarks", e);
            return;
        }
        catch (NoResponseException | XMPPErrorException e) {
            LOGGER.log(Level.WARNING, "Could not get MUC bookmarks", e);
            return;
        }

        final XMPPConnection connection = connection();
        Resourcepart defaultNick = connection.getUser().getResourcepart();

        for (BookmarkedConference bookmarkedConference : bookmarkedConferences) {
            if (!bookmarkedConference.isAutoJoin()) {
                continue;
            }
            Resourcepart nick = bookmarkedConference.getNickname();
            if (nick == null) {
                nick = defaultNick;
            }
            String password = bookmarkedConference.getPassword();
            MultiUserChat muc = multiUserChatManager.getMultiUserChat(bookmarkedConference.getJid());
            try {
                MucCreateConfigFormHandle handle = muc.createOrJoinIfNecessary(nick, password);
                if (handle != null) {
                    handle.makeInstant();
                }
            }
            catch (NotConnectedException | InterruptedException e) {
                LOGGER.log(Level.FINER, "Could not autojoin bookmarked MUC", e);
                // abort here
                break;
            }
            catch (NotAMucServiceException | NoResponseException | XMPPErrorException e) {
                // Do no abort, just log,
                LOGGER.log(Level.WARNING, "Could not autojoin bookmarked MUC", e);
            }
        }
    }
}
