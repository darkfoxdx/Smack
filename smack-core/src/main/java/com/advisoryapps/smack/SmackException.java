/**
 *
 * Copyright 2014-2019 Florian Schmaus
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
package com.advisoryapps.smack;

import java.util.ArrayList;
import java.util.List;

import com.advisoryapps.smack.filter.StanzaFilter;
import com.advisoryapps.smack.util.dns.HostAddress;

import org.jxmpp.jid.Jid;

/**
 * Smack uses SmackExceptions for errors that are not defined by any XMPP specification.
 *
 * @author Florian Schmaus
 */
public abstract class SmackException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1844674365368214458L;

    /**
     * Creates a new SmackException with the Throwable that was the root cause of the exception.
     *
     * @param wrappedThrowable the root cause of the exception.
     */
    protected SmackException(Throwable wrappedThrowable) {
        super(wrappedThrowable);
    }

    protected SmackException(String message) {
        super(message);
    }

    protected SmackException(String message, Throwable wrappedThrowable) {
        super(message, wrappedThrowable);
    }

    protected SmackException() {
    }

    /**
     * Exception thrown always when there was no response to an request within the stanza reply timeout of the used
     * connection instance. You can modify (e.g. increase) the stanza reply timeout with
     * {@link XMPPConnection#setReplyTimeout(long)}.
     */
    public static final class NoResponseException extends SmackException {
        /**
         *
         */
        private static final long serialVersionUID = -6523363748984543636L;

        private final StanzaFilter filter;

        private NoResponseException(String message) {
            this(message, null);
        }

        private NoResponseException(String message, StanzaFilter filter) {
            super(message);
            this.filter = filter;
        }

        /**
         * Get the filter that was used to collect the response.
         *
         * @return the used filter or <code>null</code>.
         */
        public StanzaFilter getFilter() {
            return filter;
        }

        public static NoResponseException newWith(XMPPConnection connection, String waitingFor) {
            final StringBuilder sb = getWaitingFor(connection);
            sb.append(" While waiting for ").append(waitingFor);
            return new NoResponseException(sb.toString());
        }

        @Deprecated
        // TODO: Remove in Smack 4.4.
        public static NoResponseException newWith(long timeout,
                StanzaCollector collector) {
            return newWith(timeout, collector.getStanzaFilter(), false);
        }

        public static NoResponseException newWith(long timeout,
                        StanzaCollector collector, boolean stanzaCollectorCancelled) {
            return newWith(timeout, collector.getStanzaFilter(), stanzaCollectorCancelled);
        }

        public static NoResponseException newWith(XMPPConnection connection, StanzaFilter filter) {
            return newWith(connection.getReplyTimeout(), filter, false);
        }

        public static NoResponseException newWith(long timeout, StanzaFilter filter, boolean stanzaCollectorCancelled) {
            final StringBuilder sb = getWaitingFor(timeout);
            if (stanzaCollectorCancelled) {
                sb.append(" StanzaCollector has been cancelled.");
            }
            sb.append(" Waited for response using: ");
            if (filter != null) {
                sb.append(filter.toString());
            }
            else {
                sb.append("No filter used or filter was 'null'");
            }
            sb.append('.');
            return new NoResponseException(sb.toString(), filter);
        }

        private static StringBuilder getWaitingFor(XMPPConnection connection) {
            return getWaitingFor(connection.getReplyTimeout());
        }

        private static StringBuilder getWaitingFor(final long replyTimeout) {
            final StringBuilder sb = new StringBuilder(256);
            sb.append("No response received within reply timeout. Timeout was "
                            + replyTimeout + "ms (~"
                            + replyTimeout / 1000 + "s).");
            return sb;
        }
    }

    public static class NotLoggedInException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = 3216216839100019278L;

        public NotLoggedInException() {
            super("Client is not logged in");
        }
    }

    public static class AlreadyLoggedInException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = 5011416918049935231L;

        public AlreadyLoggedInException() {
            super("Client is already logged in");
        }
    }

    public static class AlreadyConnectedException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = 5011416918049135231L;

        public AlreadyConnectedException() {
            super("Client is already connected");
        }
    }

    public static class NotConnectedException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = 9197980400776001173L;

        public NotConnectedException() {
            this(null);
        }

        public NotConnectedException(String optionalHint) {
            super("Client is not, or no longer, connected."
                            + (optionalHint != null ? ' ' + optionalHint : ""));
        }

        public NotConnectedException(XMPPConnection connection, String details) {
            super("The connection " + connection.toString() + " is no longer connected. "
                            + details);
        }

        public NotConnectedException(XMPPConnection connection, StanzaFilter stanzaFilter) {
            super("The connection " + connection
                            + " is no longer connected while waiting for response with " + stanzaFilter);
        }

        public NotConnectedException(XMPPConnection connection, StanzaFilter stanzaFilter,
                        Exception connectionException) {
            super("The connection " + connection + " is no longer connected while waiting for response with "
                            + stanzaFilter + " because of " + connectionException, connectionException);
        }
    }

    public static class IllegalStateChangeException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = -1766023961577168927L;

        public IllegalStateChangeException() {
        }
    }

    public abstract static class SecurityRequiredException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = 384291845029773545L;

        public SecurityRequiredException(String message) {
            super(message);
        }
    }

    public static class SecurityRequiredByClientException extends SecurityRequiredException {
        /**
         *
         */
        private static final long serialVersionUID = 2395325821201543159L;

        public SecurityRequiredByClientException() {
            super("SSL/TLS required by client but not supported by server");
        }
    }

    public static class SecurityRequiredByServerException extends SecurityRequiredException {
        /**
         *
         */
        private static final long serialVersionUID = 8268148813117631819L;

        public SecurityRequiredByServerException() {
            super("SSL/TLS required by server but disabled in client");
        }
    }

    public static class SecurityNotPossibleException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = -6836090872690331336L;

        public SecurityNotPossibleException(String message) {
            super(message);
        }
    }

    /**
     * ConnectionException is thrown if Smack is unable to connect to all hosts of a given XMPP
     * service. The failed hosts can be retrieved with
     * {@link ConnectionException#getFailedAddresses()}, which will have the exception causing the
     * connection failure set and retrievable with {@link HostAddress#getExceptions()}.
     */
    public static class ConnectionException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = 1686944201672697996L;

        private final List<HostAddress> failedAddresses;

        public ConnectionException(Throwable wrappedThrowable) {
            super(wrappedThrowable);
            failedAddresses = new ArrayList<>(0);
        }

        private ConnectionException(String message, List<HostAddress> failedAddresses) {
            super(message);
            this.failedAddresses = failedAddresses;
        }

        public static ConnectionException from(List<HostAddress> failedAddresses) {
            final String DELIMITER = ", ";
            StringBuilder sb = new StringBuilder("The following addresses failed: ");
            for (HostAddress hostAddress : failedAddresses) {
                sb.append(hostAddress.getErrorMessage());
                sb.append(DELIMITER);
            }
            // Remove the last delimiter
            sb.setLength(sb.length() - DELIMITER.length());
            return new ConnectionException(sb.toString(), failedAddresses);
        }

        public List<HostAddress> getFailedAddresses() {
            return failedAddresses;
        }
    }

    public static class ConnectionUnexpectedTerminatedException extends SmackException {

        private static final long serialVersionUID = 1L;

        public ConnectionUnexpectedTerminatedException(Throwable wrappedThrowable) {
            super(wrappedThrowable);
        }
    }

    public static class FeatureNotSupportedException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = 4713404802621452016L;

        private final String feature;
        private final Jid jid;

        public FeatureNotSupportedException(String feature) {
            this(feature, null);
        }

        public FeatureNotSupportedException(String feature, Jid jid) {
            super(feature + " not supported" + (jid == null ? "" : " by '" + jid + "'"));
            this.jid = jid;
            this.feature = feature;
        }

        /**
         * Get the feature which is not supported.
         *
         * @return the feature which is not supported
         */
        public String getFeature() {
            return feature;
        }

        /**
         * Get JID which does not support the feature. The JID can be null in cases when there are
         * multiple JIDs queried for this feature.
         *
         * @return the JID which does not support the feature, or null
         */
        public Jid getJid() {
            return jid;
        }
    }

    public static class ResourceBindingNotOfferedException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = 2346934138253437571L;

        public ResourceBindingNotOfferedException() {
            super("Resource binding was not offered by server");
        }
    }

    /**
     * A Smack exception wrapping another exception. Note that usage of this class is consider bad practice. This class
     * will eventually be marked deprecated and removed.
     */
    public static class SmackWrappedException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public SmackWrappedException(Exception exception) {
            super(exception);
        }

        public SmackWrappedException(String message, Exception exception) {
            super(message, exception);
        }
    }

    /**
     * A Smack exception wrapping a text message. Note that usage of this class is consider bad practice. This class
     * will eventually be marked deprecated and removed.
     */
    public static class SmackMessageException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public SmackMessageException(String message) {
            super(message);
        }
    }

    public static class SmackSaslException extends SmackException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public SmackSaslException(Exception exception) {
            super(exception);
        }

        public SmackSaslException(String message) {
            super(message);
        }

        public SmackSaslException(String message, Exception exception) {
            super(message, exception);
        }
    }
}
