/**
 *
 * Copyright © 2014-2019 Florian Schmaus
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
package com.advisoryapps.smack.packet;

import com.advisoryapps.smack.SmackConfiguration;

public class TestIQ extends SimpleIQ {

    public TestIQ() {
        this(SmackConfiguration.SMACK_URL_STRING, "test-iq");
    }

    public TestIQ(String element, String namespace) {
        super(element, namespace);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        if (getChildElementName() == null)
            return null;
        return super.getIQChildElementBuilder(xml);
    }
}
