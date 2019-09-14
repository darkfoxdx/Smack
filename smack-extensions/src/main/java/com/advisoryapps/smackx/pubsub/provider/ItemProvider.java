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

import java.io.IOException;

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.provider.ProviderManager;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.pubsub.Item;
import com.advisoryapps.smackx.pubsub.Item.ItemNamespace;
import com.advisoryapps.smackx.pubsub.PayloadItem;
import com.advisoryapps.smackx.pubsub.SimplePayload;
import com.advisoryapps.smackx.pubsub.packet.PubSubNamespace;

/**
 * Parses an <b>item</b> element as is defined in both the {@link PubSubNamespace#basic} and
 * {@link PubSubNamespace#event} namespaces. To parse the item contents, it will use whatever
 * {@link ExtensionElementProvider} is registered in <b>smack.providers</b> for its element name and namespace. If no
 * provider is registered, it will return a {@link SimplePayload}.
 *
 * @author Robin Collier
 */
public class ItemProvider extends ExtensionElementProvider<Item>  {
    @Override
    public Item parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment)
                    throws XmlPullParserException, IOException, SmackParsingException {
        String id = parser.getAttributeValue(null, "id");
        String node = parser.getAttributeValue(null, "node");
        String xmlns = parser.getNamespace();
        ItemNamespace itemNamespace = ItemNamespace.fromXmlns(xmlns);

        XmlPullParser.Event tag = parser.next();

        if (tag == XmlPullParser.Event.END_ELEMENT)  {
            return new Item(itemNamespace, id, node);
        }
        else {
            String payloadElemName = parser.getName();
            String payloadNS = parser.getNamespace();

            final ExtensionElementProvider<ExtensionElement> extensionProvider = ProviderManager.getExtensionProvider(payloadElemName, payloadNS);
            if (extensionProvider == null) {
                // TODO: Should we use StandardExtensionElement in this case? And probably remove SimplePayload all together.
                CharSequence payloadText = PacketParserUtils.parseElement(parser, true);
                return new PayloadItem<>(itemNamespace, id, node, new SimplePayload(payloadText.toString()));
            }
            else {
                return new PayloadItem<>(itemNamespace, id, node, extensionProvider.parse(parser));
            }
        }
    }

}
