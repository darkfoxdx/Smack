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
package com.advisoryapps.smack.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CloseableUtil {

    public static void maybeClose(Closeable closable) {
        maybeClose(closable, null);
    }

    public static void maybeClose(Closeable closable, Logger logger) {
        if (closable == null) {
            return;
        }

        try {
            closable.close();
        } catch (IOException e) {
            if (logger != null) {
                logger.log(Level.WARNING, "Could not close " + closable, e);
            }
        }
    }
}
