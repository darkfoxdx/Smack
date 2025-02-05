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

import com.advisoryapps.smack.util.ReaderListener;
import com.advisoryapps.smack.util.WriterListener;

import org.slf4j.Logger;

class SLF4JRawXmlListener implements ReaderListener, WriterListener {
    private final Logger logger;

    SLF4JRawXmlListener(Logger logger) {
        this.logger = Validate.notNull(logger);
    }

    @Override
    public void read(String str) {
        logger.debug("{}: {}", SLF4JSmackDebugger.RECEIVED_TAG, str);
    }

    @Override
    public void write(String str) {
        logger.debug("{}: {}", SLF4JSmackDebugger.SENT_TAG, str);
    }
}
