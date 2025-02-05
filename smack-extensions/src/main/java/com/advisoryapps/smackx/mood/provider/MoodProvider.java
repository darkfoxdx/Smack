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
package com.advisoryapps.smackx.mood.provider;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.ExtensionElementProvider;
import com.advisoryapps.smack.provider.ProviderManager;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.mood.Mood;
import com.advisoryapps.smackx.mood.element.MoodConcretisation;
import com.advisoryapps.smackx.mood.element.MoodElement;

public class MoodProvider extends ExtensionElementProvider<MoodElement> {

    private static final Logger LOGGER = Logger.getLogger(MoodProvider.class.getName());
    public static final MoodProvider INSTANCE = new MoodProvider();

    @Override
    public MoodElement parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment)
                    throws XmlPullParserException, IOException, SmackParsingException {
        String text = null;
        Mood mood = null;
        MoodConcretisation concretisation = null;

        outerloop: while (true) {
            XmlPullParser.Event tag = parser.next();
            String name = parser.getName();
            String namespace = parser.getNamespace();

            switch (tag) {
                case START_ELEMENT:
                    if (MoodElement.ELEM_TEXT.equals(name)) {
                        text = parser.nextText();
                        continue outerloop;
                    }

                    if (!MoodElement.NAMESPACE.equals(namespace)) {
                        LOGGER.log(Level.FINE, "Foreign namespace " + namespace + " detected. Try to find suitable MoodConcretisationProvider.");
                        MoodConcretisationProvider<?> provider = (MoodConcretisationProvider) ProviderManager.getExtensionProvider(name, namespace);
                        if (provider != null) {
                            concretisation = provider.parse(parser);
                        } else {
                            LOGGER.log(Level.FINE, "No provider for <" + name + " xmlns:'" + namespace + "'/> found. Ignore.");
                        }
                        continue outerloop;
                    }

                    try {
                        mood = Mood.valueOf(name);
                        continue outerloop;
                    } catch (IllegalArgumentException e) {
                        throw new XmlPullParserException("Unknown mood value: " + name + " encountered.");
                    }

                case END_ELEMENT:
                    if (MoodElement.ELEMENT.equals(name)) {
                        MoodElement.MoodSubjectElement subjectElement = (mood == null && concretisation == null) ?
                                null : new MoodElement.MoodSubjectElement(mood, concretisation);
                        return new MoodElement(subjectElement, text);
                    }
                    break;

                default:
                    // Catch all for incomplete switch (MissingCasesInEnumSwitch) statement.
                    break;
            }
        }
    }
}
