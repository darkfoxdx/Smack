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
package com.advisoryapps.smackx.pubsub;

import java.util.Locale;

import com.advisoryapps.smackx.pubsub.packet.PubSubNamespace;

/**
 * The types of forms supported by the PubSub specification.
 *
 * @author Robin Collier
 */
public enum FormNodeType {
    /** Form for configuring an existing node. */
    CONFIGURE_OWNER,

    /** Form for configuring a node during creation. */
    CONFIGURE,

    /** Form for configuring subscription options. */
    OPTIONS,

    /** Form which represents the default node configuration options. */
    DEFAULT;

    public PubSubElementType getNodeElement() {
        return PubSubElementType.valueOf(toString());
    }

    public static FormNodeType valueOfFromElementName(String elem, String configNamespace) {
        if ("configure".equals(elem) && PubSubNamespace.owner.getXmlns().equals(configNamespace)) {
            return CONFIGURE_OWNER;
        }
        return valueOf(elem.toUpperCase(Locale.US));
    }
}
