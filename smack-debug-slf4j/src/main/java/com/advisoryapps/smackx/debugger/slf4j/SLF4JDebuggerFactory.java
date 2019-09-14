/**
 *
 * Copyright 2014 Vyacheslav Blinov
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

package com.advisoryapps.smackx.debugger.slf4j;

import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.debugger.SmackDebugger;
import com.advisoryapps.smack.debugger.SmackDebuggerFactory;

/**
 * Implementation of SmackDebuggerFactory which always creates instance of SLF4JSmackDebugger.
 */
public final class SLF4JDebuggerFactory implements SmackDebuggerFactory {

    public static final SLF4JDebuggerFactory INSTANCE = new SLF4JDebuggerFactory();

    private SLF4JDebuggerFactory() {
    }

    @Override
    public SmackDebugger create(XMPPConnection connection) throws IllegalArgumentException {
        return new SLF4JSmackDebugger(connection);
    }
}
