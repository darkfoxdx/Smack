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
package com.advisoryapps.smack.packet;

import com.advisoryapps.smack.util.Objects;

public class ErrorIQ extends SimpleIQ {

    public static final String ELEMENT = StanzaError.ERROR;

    /**
     * Constructs a new error IQ.
     * <p>
     * According to RFC 6120 § 8.3.1 "4. An error stanza MUST contain an &lt;error/&gt; child element.", so the xmppError argument is mandatory.
     * </p>
     * @param xmppErrorBuilder the XMPPError builder (required).
     */
    public ErrorIQ(StanzaError.Builder xmppErrorBuilder) {
        super(ELEMENT, null);
        Objects.requireNonNull(xmppErrorBuilder, "xmppErrorBuilder must not be null");
        setType(IQ.Type.error);
        setError(xmppErrorBuilder);
    }

}
