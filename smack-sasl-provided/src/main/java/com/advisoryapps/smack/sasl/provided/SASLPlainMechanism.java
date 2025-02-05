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
package com.advisoryapps.smack.sasl.provided;

import javax.security.auth.callback.CallbackHandler;

import com.advisoryapps.smack.sasl.SASLMechanism;
import com.advisoryapps.smack.util.ByteUtils;

public class SASLPlainMechanism extends SASLMechanism {

    public static final String NAME = PLAIN;

    @Override
    protected void authenticateInternal(CallbackHandler cbh) {
        throw new UnsupportedOperationException("CallbackHandler not (yet) supported");
    }

    @Override
    protected byte[] getAuthenticationText() {
        // concatenate and encode username (authcid) and password
        String authzid;
        if (authorizationId == null) {
          authzid = "";
        } else {
          authzid = authorizationId.toString();
        }
        byte[] authcid = toBytes(authzid + '\u0000' + authenticationId);
        byte[] passw = toBytes('\u0000' + password);

        return ByteUtils.concat(authcid, passw);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getPriority() {
        return 410;
    }

    @Override
    public SASLPlainMechanism newInstance() {
        return new SASLPlainMechanism();
    }

    @Override
    public void checkIfSuccessfulOrThrow() {
        // No check performed
    }

    @Override
    public boolean authzidSupported() {
      return true;
    }
}
