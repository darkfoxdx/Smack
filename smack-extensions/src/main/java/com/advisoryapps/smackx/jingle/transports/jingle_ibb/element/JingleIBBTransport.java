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
package com.advisoryapps.smackx.jingle.transports.jingle_ibb.element;

import com.advisoryapps.smack.util.StringUtils;
import com.advisoryapps.smack.util.XmlStringBuilder;

import com.advisoryapps.smackx.jingle.element.JingleContentTransport;

/**
 * Transport Element for JingleInBandBytestream transports.
 */
public class JingleIBBTransport extends JingleContentTransport {
    public static final String NAMESPACE_V1 = "urn:xmpp:jingle:transports:ibb:1";
    public static final String ATTR_BLOCK_SIZE = "block-size";
    public static final String ATTR_SID = "sid";

    public static final short DEFAULT_BLOCK_SIZE = 4096;

    private final short blockSize;
    private final String sid;

    public JingleIBBTransport() {
        this(DEFAULT_BLOCK_SIZE);
    }

    public JingleIBBTransport(String sid) {
        this(DEFAULT_BLOCK_SIZE, sid);
    }

    public JingleIBBTransport(short blockSize) {
        this(blockSize, StringUtils.randomString(24));
    }

    public JingleIBBTransport(short blockSize, String sid) {
        super(null);
        if (blockSize > 0) {
            this.blockSize = blockSize;
        } else {
            this.blockSize = DEFAULT_BLOCK_SIZE;
        }
        this.sid = sid;
    }

    public String getSessionId() {
        return sid;
    }

    public short getBlockSize() {
        return blockSize;
    }

    @Override
    protected void addExtraAttributes(XmlStringBuilder xml) {
        xml.attribute(ATTR_BLOCK_SIZE, blockSize);
        xml.attribute(ATTR_SID, sid);
    }

    @Override
    public String getNamespace() {
        return NAMESPACE_V1;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof JingleIBBTransport)) {
            return false;
        }

        if (this == other) {
            return true;
        }

        JingleIBBTransport otherTransport = (JingleIBBTransport) other;
        return this.getSessionId().equals(otherTransport.getSessionId()) &&
            this.getBlockSize() == otherTransport.getBlockSize();
    }

    @Override
    public int hashCode() {
        return this.toXML().toString().hashCode();
    }
}
