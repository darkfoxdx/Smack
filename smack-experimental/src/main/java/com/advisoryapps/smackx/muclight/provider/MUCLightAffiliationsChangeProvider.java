/**
 *
 * Copyright 2016 Fernando Ramirez
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
package com.advisoryapps.smackx.muclight.provider;

import java.io.IOException;
import java.util.HashMap;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.muclight.MUCLightAffiliation;
import com.advisoryapps.smackx.muclight.element.MUCLightElements.AffiliationsChangeExtension;

import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

/**
 * MUC Light Affiliations Change Provider class.
 *
 * @author Fernando Ramirez
 *
 */
public class MUCLightAffiliationsChangeProvider extends ExtensionElementProvider<AffiliationsChangeExtension> {

    @Override
    public AffiliationsChangeExtension parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        HashMap<Jid, MUCLightAffiliation> affiliations = new HashMap<>();
        String prevVersion = null;
        String version = null;

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();

            if (eventType == XmlPullParser.Event.START_ELEMENT) {

                if (parser.getName().equals("prev-version")) {
                    prevVersion = parser.nextText();
                }

                if (parser.getName().equals("version")) {
                    version = parser.nextText();
                }

                if (parser.getName().equals("user")) {
                    MUCLightAffiliation mucLightAffiliation = MUCLightAffiliation
                            .fromString(parser.getAttributeValue("", "affiliation"));
                    Jid jid = JidCreate.from(parser.nextText());
                    affiliations.put(jid, mucLightAffiliation);
                }

            } else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getDepth() == initialDepth) {
                    break outerloop;
                }
            }
        }

        return new AffiliationsChangeExtension(affiliations, prevVersion, version);
    }

}
