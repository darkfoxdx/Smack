/**
 *
 * Copyright © 2016 Florian Schmaus
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
package com.advisoryapps.smackx.iot.provisioning.provider;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;

import com.advisoryapps.smackx.iot.provisioning.element.Friend;

import org.jxmpp.jid.BareJid;
import org.jxmpp.stringprep.XmppStringprepException;

public class FriendProvider extends ExtensionElementProvider<Friend> {

    @Override
    public Friend parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmppStringprepException {
        BareJid jid = ParserUtils.getBareJidAttribute(parser);
        return new Friend(jid);
    }

}
