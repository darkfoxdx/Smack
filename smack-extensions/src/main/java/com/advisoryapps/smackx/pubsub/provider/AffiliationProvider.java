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
package com.advisoryapps.smackx.pubsub.provider;

import java.io.IOException;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;

import com.advisoryapps.smackx.pubsub.Affiliation;
import com.advisoryapps.smackx.pubsub.Affiliation.AffiliationNamespace;

import org.jxmpp.jid.BareJid;

/**
 * Parses the affiliation element out of the reply stanza from the server
 * as specified in the <a href="http://xmpp.org/extensions/xep-0060.html#schemas-pubsub">affiliation schema</a>.
 *
 * @author Robin Collier
 */
public class AffiliationProvider extends ExtensionElementProvider<Affiliation> {

    @Override
    public Affiliation parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws IOException {
        String node = parser.getAttributeValue(null, "node");
        BareJid jid = ParserUtils.getBareJidAttribute(parser);
        String namespaceString = parser.getNamespace();
        AffiliationNamespace namespace = AffiliationNamespace.fromXmlns(namespaceString);

        String affiliationString = parser.getAttributeValue(null, "affiliation");
        Affiliation.Type affiliationType = null;
        if (affiliationString != null) {
            affiliationType = Affiliation.Type.valueOf(affiliationString);
        }
        Affiliation affiliation;
        if (node != null && jid == null) {
            // affiliationType may be empty
            affiliation = new Affiliation(node, affiliationType, namespace);
        }
        else if (node == null && jid != null) {
            affiliation = new Affiliation(jid, affiliationType, namespace);
        }
        else {
            // TODO: Should be SmackParsingException.
            throw new IOException("Invalid affililation. Either one of 'node' or 'jid' must be set"
                    + ". Node: " + node
                    + ". Jid: " + jid
                    + '.');
        }
        return affiliation;
    }

}
