/**
 *
 * Copyright © 2014 Florian Schmaus
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
package com.advisoryapps.smack.sm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.packet.Element;
import com.advisoryapps.smack.packet.Stanza;

public abstract class StreamManagementException extends SmackException {

    public StreamManagementException() {
    }

    public StreamManagementException(String message) {
        super(message);
    }

    /**
     *
     */
    private static final long serialVersionUID = 3767590115788821101L;

    public static class StreamManagementNotEnabledException extends StreamManagementException {

        /**
         *
         */
        private static final long serialVersionUID = 2624821584352571307L;

    }

    public static class StreamIdDoesNotMatchException extends StreamManagementException {

        /**
         *
         */
        private static final long serialVersionUID = 1191073341336559621L;

        public StreamIdDoesNotMatchException(String expected, String got) {
            super("Stream IDs do not match. Expected '" + expected + "', but got '" + got + "'");
        }
    }

    public static class StreamManagementCounterError extends StreamManagementException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final long handledCount;
        private final long previousServerHandledCount;
        private final long ackedStanzaCount;
        private final int outstandingStanzasCount;
        private final List<Stanza> ackedStanzas;

        public StreamManagementCounterError(long handledCount, long previousServerHandlerCount,
                        long ackedStanzaCount,
                        List<Stanza> ackedStanzas) {
            super(
                            "There was an error regarding the Stream Management counters. Server reported "
                                            + handledCount
                                            + " handled stanzas, which means that the "
                                            + ackedStanzaCount
                                            + " recently send stanzas by client are now acked by the server. But Smack had only "
                                            + ackedStanzas.size()
                                            + " to acknowledge. The stanza id of the last acked outstanding stanza is "
                                            + (ackedStanzas.isEmpty() ? "<no acked stanzas>"
                                                            : ackedStanzas.get(ackedStanzas.size() - 1).getStanzaId()));
            this.handledCount = handledCount;
            this.previousServerHandledCount = previousServerHandlerCount;
            this.ackedStanzaCount = ackedStanzaCount;
            this.outstandingStanzasCount = ackedStanzas.size();
            this.ackedStanzas = Collections.unmodifiableList(ackedStanzas);
        }

        public long getHandledCount() {
            return handledCount;
        }

        public long getPreviousServerHandledCount() {
            return previousServerHandledCount;
        }

        public long getAckedStanzaCount() {
            return ackedStanzaCount;
        }

        public int getOutstandingStanzasCount() {
            return outstandingStanzasCount;
        }

        public List<Stanza> getAckedStanzas() {
            return ackedStanzas;
        }
    }

    public static final class UnacknowledgedQueueFullException extends StreamManagementException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final int overflowElementNum;
        private final int droppedElements;
        private final List<Element> elements;
        private final List<Stanza> unacknowledgesStanzas;

        private UnacknowledgedQueueFullException(String message, int overflowElementNum, int droppedElements, List<Element> elements,
                List<Stanza> unacknowledgesStanzas) {
            super(message);
            this.overflowElementNum = overflowElementNum;
            this.droppedElements = droppedElements;
            this.elements = elements;
            this.unacknowledgesStanzas = unacknowledgesStanzas;
        }

        public int getOverflowElementNum() {
            return overflowElementNum;
        }

        public int getDroppedElements() {
            return droppedElements;
        }

        public List<Element> getElements() {
            return elements;
        }

        public List<Stanza> getUnacknowledgesStanzas() {
            return unacknowledgesStanzas;
        }

        public static UnacknowledgedQueueFullException newWith(int overflowElementNum, List<Element> elements,
                BlockingQueue<Stanza> unacknowledgedStanzas) {
            final int unacknowledgesStanzasQueueSize = unacknowledgedStanzas.size();
            List<Stanza> localUnacknowledgesStanzas = new ArrayList<>(unacknowledgesStanzasQueueSize);
            localUnacknowledgesStanzas.addAll(unacknowledgedStanzas);
            int droppedElements = elements.size() - overflowElementNum - 1;

            String message = "The queue size " + unacknowledgesStanzasQueueSize + " is not able to fit another "
                    + droppedElements + " potential stanzas type top-level stream-elements.";
            return new UnacknowledgedQueueFullException(message, overflowElementNum, droppedElements, elements,
                    localUnacknowledgesStanzas);
        }
    }
}

