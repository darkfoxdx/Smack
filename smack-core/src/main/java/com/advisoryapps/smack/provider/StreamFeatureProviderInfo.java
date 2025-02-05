/**
 *
 * Copyright © 2014 Florian Schmaus
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
package com.advisoryapps.smack.provider;

import com.advisoryapps.smack.packet.ExtensionElement;

public final class StreamFeatureProviderInfo extends AbstractProviderInfo {

    /**
     * Defines an extension provider which implements the <code>StreamFeatureProvider</code> interface.
     *
     * @param elementName Element that provider parses.
     * @param namespace Namespace that provider parses.
     * @param extProvider The provider implementation.
     */
    public StreamFeatureProviderInfo(String elementName, String namespace,
                    ExtensionElementProvider<ExtensionElement> extProvider) {
        super(elementName, namespace, extProvider);
    }

}
