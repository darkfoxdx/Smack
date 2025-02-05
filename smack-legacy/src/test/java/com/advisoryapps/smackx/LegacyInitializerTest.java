/**
 *
 * Copyright the original author or authors
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
package com.advisoryapps.smackx;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.advisoryapps.smack.legacy.LegacyInitializer;

import org.junit.jupiter.api.Test;

public class LegacyInitializerTest {

    @Test
    public void testWorkgroupProviderInitializer() {
        LegacyInitializer lpi = new LegacyInitializer();
        List<Exception> exceptions = lpi.initialize();
        assertTrue(exceptions.size() == 0);
    }
}
