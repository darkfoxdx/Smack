/**
 *
 * Copyright © 2014-2019 Florian Schmaus
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
package com.advisoryapps.smack.provider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.util.ParserUtils;

import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

public class IntrospectionProvider{

    // Unfortunately, we have to create two introspection providers, with the exactly the same code here

    public abstract static class IQIntrospectionProvider<I extends IQ> extends IQProvider<I> {
        private final Class<I> elementClass;

        protected IQIntrospectionProvider(Class<I> elementClass) {
            this.elementClass = elementClass;
        }

        @SuppressWarnings("unchecked")
        @Override
        public I parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
            try {
                return (I) parseWithIntrospection(elementClass, parser, initialDepth);
            }
            catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                            | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
                // TODO: Should probably be SmackParsingException (once it exists).
                throw new IOException(e);
            }
        }
    }

    public abstract static class PacketExtensionIntrospectionProvider<PE extends ExtensionElement> extends ExtensionElementProvider<PE> {
        private final Class<PE> elementClass;

        protected PacketExtensionIntrospectionProvider(Class<PE> elementClass) {
            this.elementClass = elementClass;
        }

        @SuppressWarnings("unchecked")
        @Override
        public PE parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
            try {
                return (PE) parseWithIntrospection(elementClass, parser, initialDepth);
            }
            catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                            | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
                // TODO: Should probably be SmackParsingException (once it exists).
                throw new IOException(e);
            }
        }
    }

    public static Object parseWithIntrospection(Class<?> objectClass,
                    XmlPullParser parser, final int initialDepth) throws NoSuchMethodException, SecurityException,
                    InstantiationException, IllegalAccessException, XmlPullParserException,
                    IOException, IllegalArgumentException, InvocationTargetException,
                    ClassNotFoundException {
        ParserUtils.assertAtStartTag(parser);
        Object object = objectClass.getConstructor().newInstance();
        outerloop: while (true) {
            XmlPullParser.Event eventType = parser.next();
            switch (eventType) {
            case START_ELEMENT:
                String name = parser.getName();
                String stringValue = parser.nextText();
                Class<?> propertyType = object.getClass().getMethod(
                                "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1)).getReturnType();
                // Get the value of the property by converting it from a
                // String to the correct object type.
                Object value = decode(propertyType, stringValue);
                // Set the value of the bean.
                object.getClass().getMethod(
                                "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1),
                                propertyType).invoke(object, value);
                break;

            case  END_ELEMENT:
                if (parser.getDepth() == initialDepth) {
                    break outerloop;
                }
                break;
            default:
                // Catch all for incomplete switch (MissingCasesInEnumSwitch) statement.
                break;
            }
        }
        ParserUtils.assertAtEndTag(parser);
        return object;
    }

    /**
     * Decodes a String into an object of the specified type. If the object
     * type is not supported, null will be returned.
     *
     * @param type the type of the property.
     * @param value the encode String value to decode.
     * @return the String value decoded into the specified type.
     * @throws ClassNotFoundException
     */
    private static Object decode(Class<?> type, String value) throws ClassNotFoundException {
        String name = type.getName();
        switch (name) {
        case "java.lang.String":
            return value;
        case "boolean":
            // CHECKSTYLE:OFF
            return Boolean.valueOf(value);
            // CHECKSTYLE:ON
        case "int":
            return Integer.valueOf(value);
        case "long":
            return Long.valueOf(value);
        case "float":
            return Float.valueOf(value);
        case "double":
            return Double.valueOf(value);
        case "short":
            return Short.valueOf(value);
        case "byte":
            return Byte.valueOf(value);
        case "java.lang.Class":
            return Class.forName(value);
        }
        return null;
    }
}
