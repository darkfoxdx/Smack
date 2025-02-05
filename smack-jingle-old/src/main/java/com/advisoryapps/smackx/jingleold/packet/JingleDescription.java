/**
 *
 * Copyright 2003-2005 Jive Software.
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
package com.advisoryapps.smackx.jingleold.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.advisoryapps.smack.packet.ExtensionElement;

import com.advisoryapps.smackx.jingleold.media.PayloadType;

/**
 * Jingle content description.
 *
 * @author Alvaro Saurin
 */
public abstract class JingleDescription implements ExtensionElement {

    private static final Logger LOGGER = Logger.getLogger(JingleDescription.class.getName());

    // static

    public static final String NODENAME = "description";

    // non-static

    private final List<PayloadType> payloads = new ArrayList<>();

    /**
     * Creates a content description..
     */
    public JingleDescription() {
        super();
    }

    /**
     * Returns the XML element name of the element.
     *
     * @return the XML element name of the element.
     */
    @Override
    public String getElementName() {
        return NODENAME;
    }

    /**
     * Return the namespace.
     *
     * @return The namespace
     */
    @Override
    public abstract String getNamespace();

    /**
     * Adds a audio payload type to the packet.
     *
     * @param pt the audio payload type to add.
     */
    public void addPayloadType(final PayloadType pt) {
        synchronized (payloads) {
            if (pt == null) {
                LOGGER.severe("Null payload type");
            } else {
                payloads.add(pt);
            }
        }
    }

    /**
     * Adds a list of payloads to the packet.
     *
     * @param pts the payloads to add.
     */
    public void addAudioPayloadTypes(final List<PayloadType> pts) {
        synchronized (payloads) {
            Iterator<PayloadType> ptIter = pts.iterator();
            while (ptIter.hasNext()) {
                PayloadType.Audio pt = (PayloadType.Audio) ptIter.next();
                addPayloadType(new PayloadType.Audio(pt));
            }
        }
    }

    /**
     * Returns an Iterator for the audio payloads in the packet.
     *
     * @return an Iterator for the audio payloads in the packet.
     */
    public Iterator<PayloadType> getPayloadTypes() {
        return Collections.unmodifiableList(getPayloadTypesList()).iterator();
    }

    /**
     * Returns a list for the audio payloads in the packet.
     *
     * @return a list for the audio payloads in the packet.
     */
    public List<PayloadType> getPayloadTypesList() {
        synchronized (payloads) {
            return new ArrayList<>(payloads);
        }
    }

    /**
     * Return the list of Payload types contained in the description.
     *
     * @return a list of PayloadType.Audio
     */
    public List<PayloadType> getAudioPayloadTypesList() {
        ArrayList<PayloadType> result = new ArrayList<>();
        Iterator<PayloadType> jinglePtsIter = getPayloadTypes();

        while (jinglePtsIter.hasNext()) {
            PayloadType jpt = jinglePtsIter.next();
            if (jpt instanceof PayloadType.Audio) {
                PayloadType.Audio jpta = (PayloadType.Audio) jpt;
                result.add(jpta);
            }
        }

        return result;
    }

    /**
     * Returns a count of the audio payloads in the Jingle packet.
     *
     * @return the number of audio payloads in the Jingle packet.
     */
    public int getPayloadTypesCount() {
        synchronized (payloads) {
            return payloads.size();
        }
    }

    /**
     * Convert a Jingle description to XML.
     *
     * @return a string with the XML representation
     */
    @Override
    public String toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
        StringBuilder buf = new StringBuilder();

        synchronized (payloads) {
            if (payloads.size() > 0) {
                buf.append('<').append(getElementName());
                buf.append(" xmlns=\"").append(getNamespace()).append("\" >");

                for (PayloadType payloadType : payloads) {
                    if (payloadType != null) {
                        buf.append(payloadType.toXML());
                    }
                }
                buf.append("</").append(getElementName()).append('>');
            }
        }

        return buf.toString();
    }

    /**
     * Jingle audio description.
     */
    public static class Audio extends JingleDescription {

        public static final String NAMESPACE = "urn:xmpp:tmp:jingle:apps:rtp";

        public Audio() {
            super();
        }

        /**
         * Utility constructor, with a PayloadType.
         *
         * @param pt the payload type.
         */
        public Audio(final PayloadType pt) {
            super();
            addPayloadType(pt);
        }

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }
    }
}
