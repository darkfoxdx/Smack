/**
 *
 * Copyright 2018 Paul Schaub
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
package com.advisoryapps.smackx.chatstate;

import com.advisoryapps.smack.chat2.Chat;
import com.advisoryapps.smack.chat2.ChatManager;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smackx.chatstates.ChatState;
import com.advisoryapps.smackx.chatstates.ChatStateListener;
import com.advisoryapps.smackx.chatstates.ChatStateManager;

import org.igniterealtime.smack.inttest.AbstractSmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTestEnvironment;
import org.igniterealtime.smack.inttest.util.SimpleResultSyncPoint;
import org.junit.After;

public class ChatStateIntegrationTest extends AbstractSmackIntegrationTest {

    // Listener for composing chat state
    private final SimpleResultSyncPoint composingSyncPoint = new SimpleResultSyncPoint();
    private final ChatStateListener composingListener = new ChatStateListener() {
        @Override
        public void stateChanged(Chat chat, ChatState state, Message message) {
            if (state.equals(ChatState.composing)) {
                composingSyncPoint.signal();
            }
        }
    };

    // Listener for active chat state
    private final SimpleResultSyncPoint activeSyncPoint = new SimpleResultSyncPoint();
    private final ChatStateListener activeListener = new ChatStateListener() {
        @Override
        public void stateChanged(Chat chat, ChatState state, Message message) {
            if (state.equals(ChatState.active)) {
                activeSyncPoint.signal();
            }
        }
    };


    public ChatStateIntegrationTest(SmackIntegrationTestEnvironment environment) {
        super(environment);
    }

    @SmackIntegrationTest
    public void testChatStateListeners() throws Exception {
        ChatStateManager manOne = ChatStateManager.getInstance(conOne);
        ChatStateManager manTwo = ChatStateManager.getInstance(conTwo);

        // Add chatState listeners.
        manTwo.addChatStateListener(composingListener);
        manTwo.addChatStateListener(activeListener);

        Chat chatOne = ChatManager.getInstanceFor(conOne)
                .chatWith(conTwo.getUser().asEntityBareJid());

        // Test, if setCurrentState works and the chatState arrives
        manOne.setCurrentState(ChatState.composing, chatOne);
        composingSyncPoint.waitForResult(timeout);

        // Test, if the OutgoingMessageInterceptor successfully adds a chatStateExtension of "active" to
        // an outgoing chat message and if it arrives at the other side.
        Chat chat = ChatManager.getInstanceFor(conOne)
                .chatWith(conTwo.getUser().asEntityBareJid());
        chat.send("Hi!");
        activeSyncPoint.waitForResult(timeout);
    }

    @After
    public void cleanup() {
        ChatStateManager manTwo = ChatStateManager.getInstance(conTwo);
        manTwo.removeChatStateListener(composingListener);
        manTwo.removeChatStateListener(activeListener);
    }
}
