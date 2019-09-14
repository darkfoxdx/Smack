/**
 *
 * Copyright 2017-2019 Florian Schmaus, 2018 Paul Schaub.
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
package com.advisoryapps.smackx.ox.element;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.util.Objects;
import com.advisoryapps.smack.util.RandomUtil;
import com.advisoryapps.smack.util.StringUtils;
import com.advisoryapps.smack.util.XmlStringBuilder;

import org.jxmpp.jid.Jid;

/**
 * Abstract class that bundles functionality of encrypted OpenPGP content elements ({@link CryptElement},
 * {@link SigncryptElement}) together.
 */
public abstract class EncryptedOpenPgpContentElement extends OpenPgpContentElement {

    public static final String ELEM_RPAD = "rpad";

    private final String rpad;

    protected EncryptedOpenPgpContentElement(Set<? extends Jid> to, String rpad, Date timestamp, List<ExtensionElement> payload) {
        super(Objects.requireNonNullNorEmpty(
                to, "Encrypted OpenPGP content elements must have at least one 'to' attribute."),
                timestamp, payload);
        this.rpad = Objects.requireNonNull(rpad);
    }

    protected EncryptedOpenPgpContentElement(Set<? extends Jid> to, List<ExtensionElement> payload) {
        super(Objects.requireNonNullNorEmpty(
                to, "Encrypted OpenPGP content elements must have at least one 'to' attribute."),
                new Date(), payload);
        this.rpad = createRandomPadding();
    }

    private static String createRandomPadding() {
        int len = RandomUtil.nextSecureRandomInt(256);
        return StringUtils.randomString(len);
    }

    @Override
    protected void addCommonXml(XmlStringBuilder xml) {
        super.addCommonXml(xml);

        xml.openElement("rpad").escape(rpad).closeElement("rpad");
    }
}
