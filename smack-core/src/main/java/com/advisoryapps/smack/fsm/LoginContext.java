/**
 *
 * Copyright 2018 Florian Schmaus
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
package com.advisoryapps.smack.fsm;

import org.jxmpp.jid.parts.Resourcepart;

// TODO: At one point SASL authzid should be part of this.
public class LoginContext {
    final String username;
    final String password;
    final Resourcepart resource;

    LoginContext(String username, String password, Resourcepart resource) {
        this.username = username;
        this.password = password;
        this.resource = resource;
    }

}
