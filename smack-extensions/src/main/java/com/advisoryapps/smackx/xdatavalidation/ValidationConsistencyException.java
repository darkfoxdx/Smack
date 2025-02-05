/**
 *
 * Copyright 2014 Anno van Vliet
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
package com.advisoryapps.smackx.xdatavalidation;

import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement;

/**
 * Exception thrown when {@link ValidateElement} is not consistent with the business rules in XEP=0122.
 *
 * @author Anno van Vliet
 *
 */
public class ValidationConsistencyException extends IllegalArgumentException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Validation consistency exception constructor.
     * @param message TODO javadoc me please
     */
    public ValidationConsistencyException(String message) {
        super(message);
    }
}
