/**
 *
 * Copyright 2018 Paul Schaub.
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

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.util.Objects;
import com.advisoryapps.smack.util.XmlStringBuilder;
import com.advisoryapps.smack.util.stringencoder.Base64;

/**
 * Class representing a pubkey element which is used to transport OpenPGP public keys.
 *
 * @see <a href="https://xmpp.org/extensions/xep-0373.html#announcing-pubkey">
 *     XEP-0373: §4.1 The OpenPGP Public-Key Data Node</a>
 */
public class PubkeyElement implements ExtensionElement {

    public static final String NAMESPACE = OpenPgpElement.NAMESPACE;
    public static final String ELEMENT = "pubkey";
    public static final String ATTR_DATE = "date";

    private final PubkeyDataElement dataElement;
    private final Date date;

    public PubkeyElement(PubkeyDataElement dataElement, Date date) {
        this.dataElement = Objects.requireNonNull(dataElement);
        this.date = date;
    }

    /**
     * Return the &lt;data&gt; element containing the base64 encoded public key.
     *
     * @return data element
     */
    public PubkeyDataElement getDataElement() {
        return dataElement;
    }

    /**
     * Date on which the key was last modified.
     *
     * @return last modification date
     */
    public Date getDate() {
        return date;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public XmlStringBuilder toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder(this)
                .optAttribute(ATTR_DATE, date)
                .rightAngleBracket()
                .append(getDataElement())
                .closeElement(this);
        return xml;
    }

    /**
     * Element that contains the base64 encoded public key.
     */
    public static class PubkeyDataElement implements ExtensionElement {

        public static final String ELEMENT = "data";

        private final String b64Data;

        public PubkeyDataElement(String b64Data) {
            this.b64Data = Objects.requireNonNull(b64Data);
        }

        /**
         * Base64 encoded public key.
         *
         * @return the base64 encoded version of the public key.
         */
        public String getB64Data() {
            return b64Data;
        }

        private transient byte[] pubKeyBytesCache;

        public byte[] getPubKeyBytes() {
            if (pubKeyBytesCache == null) {
                pubKeyBytesCache = Base64.decode(b64Data);
            }
            return pubKeyBytesCache.clone();
        }

        @Override
        public String getElementName() {
            return ELEMENT;
        }

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }

        @Override
        public XmlStringBuilder toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
            XmlStringBuilder xml = new XmlStringBuilder(this, enclosingNamespace)
                    .rightAngleBracket()
                    .append(b64Data)
                    .closeElement(this);
            return xml;
        }
    }
}
