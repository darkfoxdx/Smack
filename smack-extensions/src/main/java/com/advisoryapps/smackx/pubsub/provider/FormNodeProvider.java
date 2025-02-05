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

import com.advisoryapps.smackx.pubsub.FormNode;
import com.advisoryapps.smackx.pubsub.FormNodeType;
import com.advisoryapps.smackx.xdata.Form;
import com.advisoryapps.smackx.xdata.packet.DataForm;

/**
 * Parses one of several elements used in PubSub that contain a form of some kind as a child element.  The
 * elements and namespaces supported is defined in {@link FormNodeType}.
 *
 * @author Robin Collier
 */
public class FormNodeProvider extends EmbeddedExtensionProvider<FormNode> {
    @Override
    protected FormNode createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content) {
        return new FormNode(FormNodeType.valueOfFromElementName(currentElement, currentNamespace), attributeMap.get("node"), new Form((DataForm) content.iterator().next()));
    }
}
