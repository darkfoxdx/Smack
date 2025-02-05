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
package com.advisoryapps.smackx.pubsub.provider;

import java.util.List;
import java.util.Map;

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.provider.EmbeddedExtensionProvider;

import com.advisoryapps.smackx.pubsub.EventElement;
import com.advisoryapps.smackx.pubsub.EventElementType;
import com.advisoryapps.smackx.pubsub.NodeExtension;

/**
 * Parses the event element out of the message stanza from
 * the server as specified in the <a href="http://xmpp.org/extensions/xep-0060.html#schemas-event">event schema</a>.
 *
 * @author Robin Collier
 */
public class EventProvider extends EmbeddedExtensionProvider<EventElement> {
    @Override
    protected EventElement createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attMap, List<? extends ExtensionElement> content) {
        // TODO: Shouldn't be an embedded extension provider.
        return new EventElement(EventElementType.valueOf(content.get(0).getElementName()), (NodeExtension) content.get(0));
    }
}
