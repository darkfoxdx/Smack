/**
 *
 * Copyright 2018-2019 Florian Schmaus
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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPException.FailedNonzaException;
import com.advisoryapps.smack.packet.Nonza;
import com.advisoryapps.smack.util.XmppElementUtil;

public class NonzaCallback {

    protected final AbstractXMPPConnection connection;
    protected final Map<QName, GenericElementListener<? extends Nonza>> filterAndListeners;

    private NonzaCallback(Builder builder) {
        this.connection = builder.connection;
        this.filterAndListeners = builder.filterAndListeners;
        install();
    }

    void onNonzaReceived(Nonza nonza) {
        QName key = nonza.getQName();
        GenericElementListener<? extends Nonza> nonzaListener = filterAndListeners.get(key);

        nonzaListener.processElement(nonza);
    }

    public void cancel() {
        synchronized (connection.nonzaCallbacks) {
            for (Map.Entry<QName, GenericElementListener<? extends Nonza>> entry : filterAndListeners.entrySet()) {
                QName filterKey = entry.getKey();
                NonzaCallback installedCallback = connection.nonzaCallbacks.get(filterKey);
                if (equals(installedCallback)) {
                    connection.nonzaCallbacks.remove(filterKey);
                }
            }
        }
    }

    protected void install() {
        if (filterAndListeners.isEmpty()) {
            return;
        }

        synchronized (connection.nonzaCallbacks) {
            for (QName key : filterAndListeners.keySet()) {
                connection.nonzaCallbacks.put(key, this);
            }
        }
    }

    private static final class NonzaResponseCallback<SN extends Nonza, FN extends Nonza> extends NonzaCallback {

        private SN successNonza;
        private FN failedNonza;

        private NonzaResponseCallback(Class<? extends SN> successNonzaClass, Class<? extends FN> failedNonzaClass,
                        Builder builder) {
            super(builder);

            final QName successNonzaKey = XmppElementUtil.getQNameFor(successNonzaClass);
            final QName failedNonzaKey = XmppElementUtil.getQNameFor(failedNonzaClass);

            final GenericElementListener<SN> successListener = new GenericElementListener<SN>(successNonzaClass) {
                @Override
                public void process(SN successNonza) {
                    NonzaResponseCallback.this.successNonza = successNonza;
                    notifyResponse();
                }
            };

            final GenericElementListener<FN> failedListener = new GenericElementListener<FN>(failedNonzaClass) {
                @Override
                public void process(FN failedNonza) {
                    NonzaResponseCallback.this.failedNonza = failedNonza;
                    notifyResponse();
                }
            };

            filterAndListeners.put(successNonzaKey, successListener);
            filterAndListeners.put(failedNonzaKey, failedListener);

            install();
        }

        private void notifyResponse() {
            synchronized (this) {
                notifyAll();
            }
        }

        private boolean hasReceivedSuccessOrFailedNonza() {
            return successNonza != null || failedNonza != null;
        }

        private SN waitForResponse() throws NoResponseException, InterruptedException, FailedNonzaException {
            final long deadline = System.currentTimeMillis() + connection.getReplyTimeout();
            synchronized (this) {
                while (!hasReceivedSuccessOrFailedNonza()) {
                    final long now = System.currentTimeMillis();
                    if (now >= deadline) break;
                    wait(deadline - now);
                }
            }

            if (!hasReceivedSuccessOrFailedNonza()) {
                throw NoResponseException.newWith(connection, "Nonza Listener");
            }

            if (failedNonza != null) {
                throw new XMPPException.FailedNonzaException(failedNonza);
            }

            assert successNonza != null;
            return successNonza;
        }
    }

    public static final class Builder {
        private final AbstractXMPPConnection connection;

        private Map<QName, GenericElementListener<? extends Nonza>> filterAndListeners = new HashMap<>();

        Builder(AbstractXMPPConnection connection) {
            this.connection = connection;
        }

        public <N extends Nonza> Builder listenFor(Class<? extends N> nonza, GenericElementListener<? extends N> nonzaListener) {
            QName key = XmppElementUtil.getQNameFor(nonza);
            filterAndListeners.put(key, nonzaListener);
            return this;
        }

        public NonzaCallback install() {
            return new NonzaCallback(this);
        }
    }

    static <SN extends Nonza, FN extends Nonza> SN sendAndWaitForResponse(NonzaCallback.Builder builder, Nonza nonza, Class<SN> successNonzaClass,
                    Class<FN> failedNonzaClass)
                    throws NoResponseException, NotConnectedException, InterruptedException, FailedNonzaException {
        NonzaResponseCallback<SN, FN> nonzaCallback = new NonzaResponseCallback<>(successNonzaClass,
                        failedNonzaClass, builder);

        SN successNonza;
        try {
            nonzaCallback.connection.sendNonza(nonza);
            successNonza = nonzaCallback.waitForResponse();
        }
        finally {
            nonzaCallback.cancel();
        }

        return successNonza;
    }
}
