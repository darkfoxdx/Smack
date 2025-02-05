/**
 *
 * Copyright 2018 Paul Schaub.
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
package com.advisoryapps.smackx.ox.crypto;

import com.advisoryapps.smackx.ox.element.OpenPgpElement;

import org.pgpainless.decryption_verification.OpenPgpMetadata;

/**
 * Bundle together an {@link OpenPgpElement} and {@link OpenPgpMetadata}.
 */
public class OpenPgpElementAndMetadata {

    private final OpenPgpElement element;
    private final OpenPgpMetadata metadata;

    /**
     * Constructor.
     *
     * @param element element
     * @param metadata metadata about the elements encryption
     */
    public OpenPgpElementAndMetadata(OpenPgpElement element, OpenPgpMetadata metadata) {
        this.element = element;
        this.metadata = metadata;
    }

    /**
     * Return the {@link OpenPgpElement}.
     *
     * @return element TODO javadoc me please
     */
    public OpenPgpElement getElement() {
        return element;
    }

    /**
     * Return {@link OpenPgpMetadata} about the {@link OpenPgpElement}s encryption/signatures.
     *
     * @return metadata TODO javadoc me please
     */
    public OpenPgpMetadata getMetadata() {
        return metadata;
    }
}
