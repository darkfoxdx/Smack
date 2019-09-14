/**
 *
 * Copyright 2018 Paul Schaub.
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
package com.advisoryapps.smackx.ox.listener;

import com.advisoryapps.smack.packet.Message;

import com.advisoryapps.smackx.ox.OpenPgpContact;
import com.advisoryapps.smackx.ox.element.SigncryptElement;
import com.advisoryapps.smackx.ox_im.OxMessageListener;

import org.pgpainless.decryption_verification.OpenPgpMetadata;

public interface SigncryptElementReceivedListener {

    /**
     * A {@link SigncryptElement} has been received and successfully decrypted and verified.
     * This listener is intended to be used by implementors of different OX usage profiles. In order to listen for
     * OX-IM messages, please refer to the {@link OxMessageListener} instead.
     * @param contact sender of the message
     * @param originalMessage original message containing the the {@link SigncryptElement}
     * @param signcryptElement the {@link SigncryptElement} itself
     * @param metadata metadata about the encryption and signing
     */
    void signcryptElementReceived(OpenPgpContact contact, Message originalMessage, SigncryptElement signcryptElement, OpenPgpMetadata metadata);
}
