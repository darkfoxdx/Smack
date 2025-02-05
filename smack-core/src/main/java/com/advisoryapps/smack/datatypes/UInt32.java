/**
 *
 * Copyright 2019 Florian Schmaus
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
package com.advisoryapps.smack.datatypes;

import com.advisoryapps.smack.util.NumberUtil;

/**
 * A number representing an unsigned 32-bit integer. Can be used for values with the XML schema type "xs:unsignedInt".
 */
public final class UInt32 extends Scalar {

    private static final long serialVersionUID = 1L;

    private final long number;

    private UInt32(long number) {
        super(NumberUtil.requireUInt32(number));
        this.number = number;
    }

    public long nativeRepresentation() {
        return number;
    }

    public static UInt32 from(long number) {
        return new UInt32(number);
    }
}
