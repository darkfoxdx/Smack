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

import com.advisoryapps.smack.ConnectionCreationListener;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPConnectionRegistry;

import com.advisoryapps.smackx.disco.ServiceDiscoveryManager;
import com.advisoryapps.smackx.xdata.provider.FormFieldChildElementProviderManager;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement;
import com.advisoryapps.smackx.xdatavalidation.provider.DataValidationProvider;

public class XDataValidationManager {

    static {
        FormFieldChildElementProviderManager.addFormFieldChildElementProvider(new DataValidationProvider());

        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                ServiceDiscoveryManager serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);
                serviceDiscoveryManager.addFeature(ValidateElement.NAMESPACE);
            }
        });
    }

}
