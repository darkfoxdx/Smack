/**
 *
 * Copyright 2014 Andriy Tsykholyas, 2015 Florian Schmaus
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
package com.advisoryapps.smackx.hoxt.packet;

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.util.Objects;
import com.advisoryapps.smack.util.XmlStringBuilder;

import com.advisoryapps.smackx.hoxt.HOXTManager;

/**
 * Stanza extension for base64 binary chunks.<p>
 * This class is immutable.
 *
 * @author Andriy Tsykholyas
 * @see <a href="http://xmpp.org/extensions/xep-0332.html">XEP-0332: HTTP over XMPP transport</a>
 */
public class Base64BinaryChunk implements ExtensionElement {

    public static final String ELEMENT_CHUNK = "chunk";
    public static final String ATTRIBUTE_STREAM_ID = "streamId";
    public static final String ATTRIBUTE_LAST = "last";
    public static final String ATTRIBUTE_NR = "nr";

    private final String streamId;
    private final boolean last;
    private final String text;
    private final int nr;

    /**
     * Creates the extension.
     *
     * @param text     value of text attribute
     * @param streamId value of streamId attribute
     * @param nr       value of nr attribute
     * @param last     value of last attribute
     */
    public Base64BinaryChunk(String text, String streamId, int nr, boolean last) {
        this.text = Objects.requireNonNull(text, "text must not be null");
        this.streamId = Objects.requireNonNull(streamId, "streamId must not be null");
        if (nr < 0) {
            throw new IllegalArgumentException("nr must be a non negative integer");
        }
        this.nr = nr;
        this.last = last;
    }

    /**
     * Creates the extension. Last attribute will be initialized with default value (false).
     *
     * @param text     value of text attribute
     * @param streamId value of streamId attribute
     * @param nr       value of nr attribute
     */
    public Base64BinaryChunk(String text, String streamId, int nr) {
        this(text, streamId, nr, false);
    }

    /**
     * Returns streamId attribute.
     *
     * @return streamId attribute
     */
    public String getStreamId() {
        return streamId;
    }

    /**
     * Returns last attribute.
     *
     * @return last attribute
     */
    public boolean isLast() {
        return last;
    }

    /**
     * Returns text attribute.
     *
     * @return text attribute
     */
    public String getText() {
        return text;
    }

    /**
     * Returns nr attribute.
     *
     * @return nr attribute
     */
    public int getNr() {
        return nr;
    }

    @Override
    public String getElementName() {
        return ELEMENT_CHUNK;
    }

    @Override
    public String getNamespace() {
        return HOXTManager.NAMESPACE;
    }

    @Override
    public XmlStringBuilder toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.attribute("streamId", streamId);
        xml.attribute("nr", nr);
        xml.optBooleanAttribute("last", last);
        xml.rightAngleBracket();
        xml.append(text);
        xml.closeElement(this);
        return xml;
    }
}
