/**
 *
 * Copyright 2014 Florian Schmaus
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
package com.advisoryapps.smack.tcp;

import com.advisoryapps.smack.ConnectionConfiguration;

/**
 * A connection configuration for XMPP connections over TCP (the common case).
 * <p>
 * You can get an instance of the configuration builder with {@link #builder()} and build the final immutable connection
 * configuration with {@link Builder#build()}.
 * </p>
 * <pre>
 * {@code
 * XMPPTCPConnectionConfiguration conf = XMPPConnectionConfiguration.builder()
 *     .setXmppDomain("example.org").setUsernameAndPassword("user", "password")
 *     .setCompressionEnabled(false).build();
 * XMPPTCPConnection connection = new XMPPTCPConnection(conf);
 * }
 * </pre>
 */
public final class XMPPTCPConnectionConfiguration extends ConnectionConfiguration {

    /**
     * The default connect timeout in milliseconds. Preinitialized with 30000 (30 seconds). If this value is changed,
     * new Builder instances will use the new value as default.
     */
    public static int DEFAULT_CONNECT_TIMEOUT = 30000;

    /**
     * How long the socket will wait until a TCP connection is established (in milliseconds).
     */
    private final int connectTimeout;

    private XMPPTCPConnectionConfiguration(Builder builder) {
        super(builder);
        connectTimeout = builder.connectTimeout;
    }

    /**
     * How long the socket will wait until a TCP connection is established (in milliseconds). Defaults to {@link #DEFAULT_CONNECT_TIMEOUT}.
     *
     * @return the timeout value in milliseconds.
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * A configuration builder for XMPP connections over TCP. Use {@link XMPPTCPConnectionConfiguration#builder()} to
     * obtain a new instance and {@link #build} to build the configuration.
     */
    public static final class Builder extends ConnectionConfiguration.Builder<Builder, XMPPTCPConnectionConfiguration> {
        private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

        private Builder() {
        }

        /**
         * Set how long the socket will wait until a TCP connection is established (in milliseconds).
         *
         * @param connectTimeout the timeout value to be used in milliseconds.
         * @return a reference to this object.
         */
        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public XMPPTCPConnectionConfiguration build() {
            return new XMPPTCPConnectionConfiguration(this);
        }
    }
}
