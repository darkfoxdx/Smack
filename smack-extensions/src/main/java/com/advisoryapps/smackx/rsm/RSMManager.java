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
package com.advisoryapps.smackx.rsm;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.util.PacketUtil;

import com.advisoryapps.smackx.rsm.packet.RSMSet;
import com.advisoryapps.smackx.rsm.packet.RSMSet.PageDirection;

public class RSMManager {

    Collection<ExtensionElement> page(int max) {
        List<ExtensionElement> packetExtensions = new LinkedList<>();
        packetExtensions.add(new RSMSet(max));
        return packetExtensions;
    }

    Collection<ExtensionElement> continuePage(int max, Collection<ExtensionElement> returnedExtensions) {
        return continuePage(max, returnedExtensions, null);
    }

    Collection<ExtensionElement> continuePage(int max,
                    Collection<ExtensionElement> returnedExtensions,
                    Collection<ExtensionElement> additionalExtensions) {
        if (returnedExtensions == null) {
            throw new IllegalArgumentException("returnedExtensions must no be null");
        }
        if (additionalExtensions == null) {
            additionalExtensions = new LinkedList<>();
        }
        RSMSet resultRsmSet = PacketUtil.extensionElementFrom(returnedExtensions, RSMSet.ELEMENT, RSMSet.NAMESPACE);
        if (resultRsmSet == null) {
            throw new IllegalArgumentException("returnedExtensions did not contain a RSMset");
        }
        RSMSet continuePageRsmSet = new RSMSet(max, resultRsmSet.getLast(), PageDirection.after);
        additionalExtensions.add(continuePageRsmSet);
        return additionalExtensions;
    }
}
