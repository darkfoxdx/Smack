/**
 *
 * Copyright 2017 Florian Schmaus
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
package com.advisoryapps.smackx.carbons;

import com.advisoryapps.smack.packet.Message;

import com.advisoryapps.smackx.carbons.packet.CarbonExtension.Direction;

public interface CarbonCopyReceivedListener {

    /**
     * Invoked when a new carbon copy was received.
     *
     * @param direction the direction of the carbon copy.
     * @param carbonCopy the carbon copy itself.
     * @param wrappingMessage the message wrapping the carbon copy.
     */
    void onCarbonCopyReceived(Direction direction, Message carbonCopy, Message wrappingMessage);

}
