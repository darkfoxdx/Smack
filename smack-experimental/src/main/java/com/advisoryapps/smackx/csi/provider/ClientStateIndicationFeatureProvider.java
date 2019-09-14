/**
 *
 * Copyright © 2014 Florian Schmaus
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
package com.advisoryapps.smackx.csi.provider;

import java.io.IOException;

import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.provider.ExtensionElementProvider;

import com.advisoryapps.smackx.csi.packet.ClientStateIndication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ClientStateIndicationFeatureProvider extends ExtensionElementProvider<ClientStateIndication.Feature> {

    @Override
    public ClientStateIndication.Feature parse(XmlPullParser parser, int initialDepth)
                    throws XmlPullParserException, IOException, SmackException {
        return ClientStateIndication.Feature.INSTANCE;
    }

}
