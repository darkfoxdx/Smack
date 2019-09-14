/**
 *
 * Copyright 2018-2019 Florian Schmaus
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
package com.advisoryapps.smack.compress.provider;

import java.io.IOException;
import java.util.logging.Logger;

import com.advisoryapps.smack.compress.packet.Failure;
import com.advisoryapps.smack.packet.StanzaError;
import com.advisoryapps.smack.packet.StreamOpen;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.NonzaProvider;
import com.advisoryapps.smack.util.PacketParserUtils;

import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

public final class FailureProvider extends NonzaProvider<Failure> {

    private static final Logger LOGGER = Logger.getLogger(FailureProvider.class.getName());

    public static final FailureProvider INSTANCE = new FailureProvider();

    private FailureProvider() {
    }

    @Override
    public Failure parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        Failure.CompressFailureError compressFailureError = null;
        StanzaError stanzaError = null;
        XmlEnvironment failureXmlEnvironment = XmlEnvironment.from(parser, xmlEnvironment);

        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                String name = parser.getName();
                String namespace = parser.getNamespace();
                switch (namespace) {
                case Failure.NAMESPACE:
                    compressFailureError = Failure.CompressFailureError.valueOf(name.replace("-", "_"));
                    if (compressFailureError == null) {
                        LOGGER.warning("Unknown element in " + Failure.NAMESPACE + ": " + name);
                    }
                    break;
                case StreamOpen.CLIENT_NAMESPACE:
                case StreamOpen.SERVER_NAMESPACE:
                    switch (name) {
                        case StanzaError.ERROR:
                            StanzaError.Builder stanzaErrorBuilder = PacketParserUtils.parseError(parser, failureXmlEnvironment);
                            stanzaError = stanzaErrorBuilder.build();
                            break;
                        default:
                            LOGGER.warning("Unknown element in " + namespace + ": " + name);
                            break;
                    }
                    break;
                }
                break;
            case END_ELEMENT:
                if (parser.getDepth() == initialDepth) {
                    break outerloop;
                }
                break;
            default: // fall out
            }
        }

        return new Failure(compressFailureError, stanzaError);
    }

}
