/**
 *
 * Copyright 2009 Robin Collier
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
package com.advisoryapps.smackx.pubsub;

import com.advisoryapps.smack.packet.PacketExtension;
import com.advisoryapps.smack.provider.PacketExtensionProvider;
import com.advisoryapps.smack.xml.XmlPullParser;

/**
 *
 * @author Robin Collier
 *
 */
public class CarExtensionProvider extends PacketExtensionProvider
{

	public PacketExtension parse(XmlPullParser parser, int initialDepth) throws Exception
	{
		String color = null;
		int numTires = 0;

		for (int i=0; i<2; i++)
		{
			while (parser.next() != START_ELEMENT);

			if (parser.getName().equals("paint"))
			{
				color = parser.getAttributeValue(0);
			}
			else
			{
				numTires = Integer.parseInt(parser.getAttributeValue(0));
			}
		}
		while (parser.next() != END_ELEMENT);
		return new CarExtension(color, numTires);
	}

}
