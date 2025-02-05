/**
 *
 * Copyright 2016 Fernando Ramirez
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
package com.advisoryapps.smackx.muclight;

import java.util.Locale;

/**
 * MUCLight affiliations enum.
 *
 * @author Fernando Ramirez
 *
 */
public enum MUCLightAffiliation {

    owner,
    member,
    none;

    public static MUCLightAffiliation fromString(String string) {
        if (string == null) {
            return null;
        }
        return MUCLightAffiliation.valueOf(string.toLowerCase(Locale.US));
    }

}
