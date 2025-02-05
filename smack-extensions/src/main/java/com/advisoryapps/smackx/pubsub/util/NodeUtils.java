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
package com.advisoryapps.smackx.pubsub.util;

import com.advisoryapps.smack.packet.Stanza;

import com.advisoryapps.smackx.pubsub.ConfigureForm;
import com.advisoryapps.smackx.pubsub.FormNode;
import com.advisoryapps.smackx.pubsub.PubSubElementType;
import com.advisoryapps.smackx.xdata.Form;

/**
 * Utility for extracting information from packets.
 *
 * @author Robin Collier
 */
public class NodeUtils {
    /**
     * Get a {@link ConfigureForm} from a packet.
     *
     * @param packet TODO javadoc me please
     * @param elem TODO javadoc me please
     * @return The configuration form
     */
    public static ConfigureForm getFormFromPacket(Stanza packet, PubSubElementType elem) {
        FormNode config = packet.getExtension(elem.getElementName(), elem.getNamespace().getXmlns());
        Form formReply = config.getForm();
        return new ConfigureForm(formReply);
    }
}
