/**
 *
 * Copyright © 2016 Florian Schmaus
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
package com.advisoryapps.smackx.iot.provisioning.element;

import com.advisoryapps.smack.packet.SimpleIQ;

public class ClearCacheResponse extends SimpleIQ {

    public static final String ELEMENT = "clearCacheResponse";
    public static final String NAMESPACE = Constants.IOT_PROVISIONING_NAMESPACE;

    public ClearCacheResponse() {
        super(ELEMENT, NAMESPACE);
        // <clearCacheResponse/> IQs are always of type 'result' (XEP-0324 § 3.5.1, see also the XEPs history remarks)
        setType(Type.result);
    }

    public ClearCacheResponse(ClearCache clearCacheRequest) {
        this();
        setStanzaId(clearCacheRequest.getStanzaId());
        setTo(clearCacheRequest.getFrom());
    }
}
