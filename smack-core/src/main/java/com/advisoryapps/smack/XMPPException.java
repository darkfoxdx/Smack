/**
 *
 * Copyright 2003-2007 Jive Software.
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

import com.advisoryapps.smack.packet.Nonza;
import com.advisoryapps.smack.packet.Stanza;
import com.advisoryapps.smack.packet.StanzaError;
import com.advisoryapps.smack.packet.StreamError;

import org.jxmpp.jid.Jid;

/**
 * A generic exception that is thrown when an error occurs performing an
 * XMPP operation. XMPP servers can respond to error conditions with an error code
 * and textual description of the problem, which are encapsulated in the XMPPError
 * class. When appropriate, an XMPPError instance is attached instances of this exception.<p>
 *
 * When a stream error occurred, the server will send a stream error to the client before
 * closing the connection. Stream errors are unrecoverable errors. When a stream error
 * is sent to the client an XMPPException will be thrown containing the StreamError sent
 * by the server.
 *
 * @see StanzaError
 * @author Matt Tucker
 */
public abstract class XMPPException extends Exception {
    private static final long serialVersionUID = 6881651633890968625L;


    /**
     * Creates a new XMPPException.
     */
    protected XMPPException() {
        super();
    }

    /**
     * Creates a new XMPPException with a description of the exception.
     *
     * @param message description of the exception.
     */
    protected XMPPException(String message) {
        super(message);
    }

    /**
     * Creates a new XMPPException with a description of the exception and the
     * Throwable that was the root cause of the exception.
     *
     * @param message a description of the exception.
     * @param wrappedThrowable the root cause of the exception.
     */
    protected XMPPException(String message, Throwable wrappedThrowable) {
        super(message, wrappedThrowable);
    }

    /**
     * An exception caused by an XMPP error stanza response on the protocol level. You can examine the underlying
     * {@link StanzaError} by calling {@link #getStanzaError()}.
     */
    public static class XMPPErrorException extends XMPPException {
        /**
         *
         */
        private static final long serialVersionUID = 212790389529249604L;
        private final StanzaError error;
        private final Stanza stanza;

        /**
         * The request which resulted in the XMPP protocol error response. May be {@code null}.
         */
        private final Stanza request;

        /**
         * Creates a new XMPPErrorException with the XMPPError that was the root case of the exception.
         *
         * @param stanza stanza that contained the exception.
         * @param error the root cause of the exception.
         */
        public XMPPErrorException(Stanza stanza, StanzaError error) {
            this(stanza, error, null);
        }

        /**
         * Creates a new XMPPErrorException with the XMPPError that was the root case of the exception.
         *
         * @param request the request which triggered the error.
         * @param stanza stanza that contained the exception.
         * @param error the root cause of the exception.
         * @since 4.3.0
         */
        public XMPPErrorException(Stanza stanza, StanzaError error, Stanza request) {
            super();
            this.error = error;
            this.stanza = stanza;
            this.request = request;
        }

        /**
         * Returns the stanza error extension element associated with this exception.
         *
         * @return the stanza error extension element associated with this exception.
         */
        public StanzaError getStanzaError() {
            return error;
        }

        /**
         * Get the request which triggered the error response causing this exception.
         *
         * @return the request or {@code null}.
         * @since 4.3.0
         */
        public Stanza getRequest() {
            return request;
        }

        @Override
        public String getMessage() {
            StringBuilder sb = new StringBuilder();

            if (stanza != null) {
                Jid from = stanza.getFrom();
                if (from != null) {
                    sb.append("XMPP error reply received from " + from + ": ");
                }
            }

            sb.append(error);

            if (request != null) {
                sb.append(" as result of the following request: ");
                sb.append(request);
            }

            return sb.toString();
        }

        public static void ifHasErrorThenThrow(Stanza packet) throws XMPPErrorException {
            ifHasErrorThenThrow(packet, null);
        }

        public static void ifHasErrorThenThrow(Stanza packet, Stanza request) throws XMPPErrorException {
            StanzaError xmppError = packet.getError();
            if (xmppError != null) {
                throw new XMPPErrorException(packet, xmppError, request);
            }
        }
    }

    public static class FailedNonzaException extends XMPPException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final StanzaError.Condition condition;

        private final Nonza nonza;

        public FailedNonzaException(Nonza failedNonza) {
            this(failedNonza, null);
        }

        public FailedNonzaException(Nonza nonza, StanzaError.Condition condition) {
            this.condition = condition;
            this.nonza = nonza;
        }

        public StanzaError.Condition getCondition() {
            return condition;
        }

        public Nonza getNonza() {
            return nonza;
        }
    }

    public static class StreamErrorException extends XMPPException {
        /**
         *
         */
        private static final long serialVersionUID = 3400556867134848886L;
        private final StreamError streamError;

        /**
         * Creates a new XMPPException with the stream error that was the root case of the
         * exception. When a stream error is received from the server then the underlying connection
         * will be closed by the server.
         *
         * @param streamError the root cause of the exception.
         */
        public StreamErrorException(StreamError streamError) {
            super(streamError.getCondition().toString()
                  + " You can read more about the meaning of this stream error at http://xmpp.org/rfcs/rfc6120.html#streams-error-conditions\n"
                  + streamError.toString());
            this.streamError = streamError;
        }

        /**
         * Returns the StreamError associated with this exception. The underlying TCP connection is
         * closed by the server after sending the stream error to the client.
         *
         * @return the StreamError associated with this exception.
         */
        public StreamError getStreamError() {
            return streamError;
        }

    }
}
