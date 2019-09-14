/**
 *
 * Copyright © 2015 Tomáš Havlas
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
package com.advisoryapps.smack.roster.provider;

import java.io.IOException;

import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.roster.packet.SubscriptionPreApproval;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SubscriptionPreApprovalStreamFeatureProvider extends ExtensionElementProvider<SubscriptionPreApproval> {

    @Override
    public SubscriptionPreApproval parse(XmlPullParser parser, int initialDepth)
            throws XmlPullParserException, IOException, SmackException {
        return SubscriptionPreApproval.INSTANCE;
    }

}
