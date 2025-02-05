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

import com.advisoryapps.smackx.pubsub.Subscription;
import com.advisoryapps.smackx.pubsub.SubscriptionsExtension;
import com.advisoryapps.smackx.pubsub.SubscriptionsExtension.SubscriptionsNamespace;

/**
 * Parses the <b>subscriptions</b> element out of the PubSub IQ message from
 * the server as specified in the <a href="http://xmpp.org/extensions/xep-0060.html#schemas-pubsub">subscriptions schema</a>.
 *
 * @author Robin Collier
 */
public class SubscriptionsProvider extends EmbeddedExtensionProvider<SubscriptionsExtension> {
    @SuppressWarnings("unchecked")
    @Override
    protected SubscriptionsExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
        SubscriptionsNamespace subscriptionsNamespace = SubscriptionsNamespace.fromXmlns(currentNamespace);
        String nodeId = attributeMap.get("node");
        return new SubscriptionsExtension(subscriptionsNamespace, nodeId, (List<Subscription>) content);
    }

}
