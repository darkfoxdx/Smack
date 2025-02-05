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

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.util.XmlStringBuilder;

import org.jxmpp.jid.BareJid;

public class Unfriend implements ExtensionElement {
    public static final String ELEMENT = "UNFRIEND";
    public static final String NAMESPACE = Constants.IOT_PROVISIONING_NAMESPACE;

    private final BareJid jid;

    public Unfriend(BareJid jid) {
        this.jid = jid;
    }

    public BareJid getJid() {
        return jid;
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
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.attribute("jid", jid);
        xml.closeEmptyElement();
        return xml;
    }

    public static Unfriend from(Message message) {
        return message.getExtension(ELEMENT, NAMESPACE);
    }
}
