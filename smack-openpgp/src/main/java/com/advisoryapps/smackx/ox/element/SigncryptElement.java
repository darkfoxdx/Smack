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
import com.advisoryapps.smack.util.XmlStringBuilder;

import org.jxmpp.jid.Jid;

/**
 * This class represents an OpenPGP content element which is encrypted and signed.
 *
 * @see <a href="https://xmpp.org/extensions/xep-0373.html#exchange">
 *     XEP-0373: §3.1 Exchanging OpenPGP Encrypted and Signed Data</a>
 */
public class SigncryptElement extends EncryptedOpenPgpContentElement {

    public static final String ELEMENT = "signcrypt";

    public SigncryptElement(Set<? extends Jid> to, String rpad, Date timestamp, List<ExtensionElement> payload) {
        super(to, rpad, timestamp, payload);
    }

    public SigncryptElement(Set<? extends Jid> to, List<ExtensionElement> payload) {
        super(to, payload);
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public XmlStringBuilder toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder(this).rightAngleBracket();
        addCommonXml(xml);
        xml.closeElement(this);
        return xml;
    }

}
