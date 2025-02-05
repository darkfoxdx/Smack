/**
 *
 * Copyright 2003-2007 Jive Software.
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
package com.advisoryapps.smackx.workgroup.agent;

public interface OfferConfirmationListener {


    /**
     * The implementing class instance will be notified via this when the AgentSession has confirmed
     * the acceptance of the <code>Offer</code>. The instance will then have the ability to create the room and
     * send the service the room name created for tracking.
     *
     * @param confirmedOffer the ConfirmedOffer
     */
    void offerConfirmed(OfferConfirmation confirmedOffer);
}
