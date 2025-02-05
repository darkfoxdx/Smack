/**
 *
 * Copyright 2017 Paul Schaub
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
package com.advisoryapps.smackx.jingle_filetransfer.element;

import java.util.List;

import com.advisoryapps.smackx.jingle.element.JingleContentDescription;
import com.advisoryapps.smackx.jingle.element.JingleContentDescriptionChildElement;

/**
 * File element.
 */
public class JingleFileTransfer extends JingleContentDescription {
    public static final String NAMESPACE_V5 = "urn:xmpp:jingle:apps:file-transfer:5";

    public JingleFileTransfer(List<JingleContentDescriptionChildElement> payloads) {
        super(payloads);
    }

    @Override
    public String getNamespace() {
        return NAMESPACE_V5;
    }
}
