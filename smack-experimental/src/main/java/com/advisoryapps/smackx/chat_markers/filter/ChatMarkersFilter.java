/**
 *
 * Copyright 2018 Miguel Hincapie
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
package com.advisoryapps.smackx.chat_markers.filter;

import com.advisoryapps.smack.filter.StanzaExtensionFilter;
import com.advisoryapps.smack.filter.StanzaFilter;

import com.advisoryapps.smackx.chat_markers.element.ChatMarkersElements;

/**
 * Chat Markers Manager class (XEP-0333).
 *
 * @author HINCM008 6/08/2018.
 * @see <a href="http://xmpp.org/extensions/xep-0333.html">XEP-0333: Chat
 * Markers</a>
 */
public final class ChatMarkersFilter extends StanzaExtensionFilter {

    public static final StanzaFilter INSTANCE = new ChatMarkersFilter(ChatMarkersElements.NAMESPACE);

    private ChatMarkersFilter(String namespace) {
        super(namespace);
    }
}
