/**
 *
 * Copyright 2003-2007 Jive Software, 2015-2016 Florian Schmaus.
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

import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.parts.Resourcepart;

/**
 * IQ stanza used by Smack to bind a resource and to obtain the jid assigned by the server.
 * There are two ways to bind a resource. One is simply sending an empty Bind stanza where the
 * server will assign a new resource for this connection. The other option is to set a desired
 * resource but the server may return a modified version of the sent resource.<p>
 *
 * For more information refer to the following
 * <a href=http://www.xmpp.org/specs/rfc3920.html#bind>link</a>.
 *
 * @author Gaston Dombiak
 */
public final class Bind extends IQ {

    public static final String ELEMENT = "bind";
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-bind";

    private final Resourcepart resource;
    private final EntityFullJid jid;

    private Bind(Resourcepart resource, EntityFullJid jid) {
        super(ELEMENT, NAMESPACE);
        this.resource = resource;
        this.jid = jid;
    }

    public Resourcepart getResource() {
        return resource;
    }

    public EntityFullJid getJid() {
        return jid;
    }

    public static Bind newSet(Resourcepart resource) {
        Bind bind = new Bind(resource, null);
        bind.setType(IQ.Type.set);
        return bind;
    }

    public static Bind newResult(EntityFullJid jid) {
        return new Bind(null, jid);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.optElement("resource", resource);
        xml.optElement("jid", jid);
        return xml;
    }

    public static final class Feature implements ExtensionElement {

        public static final Feature INSTANCE = new Feature();

        private Feature() {
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
        public String toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
            return '<' + ELEMENT + " xmlns='" + NAMESPACE + "'/>";
        }

    }
}
