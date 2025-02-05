/**
 *
 * Copyright 2017 Paul Schaub
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
package com.advisoryapps.smackx.jingle.transports.jingle_s5b.elements;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.util.InternetAddress;
import com.advisoryapps.smack.util.Objects;
import com.advisoryapps.smack.util.StringUtils;
import com.advisoryapps.smack.util.XmlStringBuilder;

import com.advisoryapps.smackx.bytestreams.socks5.packet.Bytestream;
import com.advisoryapps.smackx.jingle.element.JingleContentTransportCandidate;

import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * TransportCandidate for Jingle Socks5Bytestream transports.
 */
public final class JingleS5BTransportCandidate extends JingleContentTransportCandidate {

    public static final String NAMESPACE = JingleS5BTransport.NAMESPACE_V1;

    public static final String ATTR_CID = "cid";
    public static final String ATTR_HOST = "host";
    public static final String ATTR_JID = "jid";
    public static final String ATTR_PORT = "port";
    public static final String ATTR_PRIORITY = "priority";
    public static final String ATTR_TYPE = "type";

    private final String cid;
    private final InternetAddress host;
    private final Jid jid;
    private final int port;
    private final int priority;
    private final Type type;

    public JingleS5BTransportCandidate(String candidateId, String hostString, Jid jid, int port, int priority, Type type) {
        this(candidateId, InternetAddress.from(hostString), jid, port, priority, type);
    }

    public JingleS5BTransportCandidate(String candidateId, InternetAddress host, Jid jid, int port, int priority, Type type) {
        Objects.requireNonNull(candidateId);
        Objects.requireNonNull(host);
        Objects.requireNonNull(jid);

        if (priority < 0) {
            throw new IllegalArgumentException("Priority MUST NOT be less than 0.");
        }
        if (port < 0) {
            throw new IllegalArgumentException("Port MUST NOT be less than 0.");
        }

        this.cid = candidateId;
        this.host = host;
        this.jid = jid;
        this.port = port;
        this.priority = priority;
        this.type = type;
    }

    public JingleS5BTransportCandidate(Bytestream.StreamHost streamHost, int priority, Type type) {
        this(StringUtils.randomString(24), streamHost.getAddress(), streamHost.getJID(), streamHost.getPort(), priority, type);
    }

    public enum Type {
        assisted (120),
        direct (126),
        proxy (10),
        tunnel (110),
        ;

        private final int weight;

        public int getWeight() {
            return weight;
        }

        Type(int weight) {
            this.weight = weight;
        }

        public static Type fromString(String name) {
            for (Type t : Type.values()) {
                if (t.toString().equals(name)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Illegal type: " + name);
        }
    }

    public String getCandidateId() {
        return cid;
    }

    public InternetAddress getHost() {
        return host;
    }

    public Jid getJid() {
        return jid;
    }

    public int getPort() {
        return port;
    }

    public int getPriority() {
        return priority;
    }

    public Type getType() {
        return type;
    }

    public Bytestream.StreamHost getStreamHost() {
        return new Bytestream.StreamHost(jid, host, port);
    }


    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public XmlStringBuilder toXML(XmlEnvironment enclosingXmlEnvironment) {
        XmlStringBuilder xml = new XmlStringBuilder(this, enclosingXmlEnvironment);
        xml.attribute(ATTR_CID, cid);
        xml.attribute(ATTR_HOST, host);
        xml.attribute(ATTR_JID, jid);
        if (port >= 0) {
            xml.attribute(ATTR_PORT, port);
        }
        xml.attribute(ATTR_PRIORITY, priority);
        xml.optAttribute(ATTR_TYPE, type);

        xml.closeEmptyElement();
        return xml;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String cid;
        private InternetAddress host;
        private Jid jid;
        private int port = -1;
        private int priority = -1;
        private Type type;

        private Builder() {
        }

        public Builder setCandidateId(String cid) {
            this.cid = cid;
            return this;
        }

        public Builder setHost(String host)  {
            InternetAddress inetAddress = InternetAddress.from(host);
            return setHost(inetAddress);
        }

        public Builder setHost(InternetAddress host) {
            this.host = host;
            return this;
        }

        public Builder setJid(String jid) throws XmppStringprepException {
            this.jid = JidCreate.from(jid);
            return this;
        }

        public Builder setPort(int port) {
            if (port < 0) {
                throw new IllegalArgumentException("Port MUST NOT be less than 0.");
            }
            this.port = port;
            return this;
        }

        public Builder setPriority(int priority) {
            if (priority < 0) {
                throw new IllegalArgumentException("Priority MUST NOT be less than 0.");
            }
            this.priority = priority;
            return this;
        }

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public JingleS5BTransportCandidate build() {
            return new JingleS5BTransportCandidate(cid, host, jid, port, priority, type);
        }
    }

}
