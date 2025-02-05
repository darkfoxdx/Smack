/**
 *
 * Copyright © 2017 Grigory Fedorov
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
package com.advisoryapps.smackx.httpfileupload;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.advisoryapps.smackx.httpfileupload.element.FileTooLargeError;

import org.junit.jupiter.api.Test;

public class FileTooLargeErrorCreateTest {
    private static final String fileTooLargeErrorExtensionExample
            = "<file-too-large xmlns='urn:xmpp:http:upload:0'>"
            +   "<max-file-size>20000</max-file-size>"
            + "</file-too-large>";

    @Test
    public void checkFileTooLargeErrorExtensionCreation() {
        FileTooLargeError fileTooLargeError = new FileTooLargeError(20000);

        assertEquals(20000, fileTooLargeError.getMaxFileSize());
        assertEquals(fileTooLargeErrorExtensionExample, fileTooLargeError.toXML().toString());

    }

}
