/**
 *
 * Copyright 2011 Robin Collier
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
package com.advisoryapps.smackx.muc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.advisoryapps.smackx.disco.packet.DiscoverInfo;
import com.advisoryapps.smackx.xdata.FormField;
import com.advisoryapps.smackx.xdata.packet.DataForm;

import org.junit.Test;

public class RoomInfoTest {
    @Test
    public void validateRoomWithEmptyForm() {
        DataForm dataForm = new DataForm(DataForm.Type.result);

        DiscoverInfo discoInfo = new DiscoverInfo();
        discoInfo.addExtension(dataForm);
        RoomInfo roomInfo = new RoomInfo(discoInfo);
        assertTrue(roomInfo.getDescription().isEmpty());
        assertTrue(roomInfo.getSubject().isEmpty());
        assertEquals(-1, roomInfo.getOccupantsCount());
    }

    @Test
    public void validateRoomWithForm() {
        DataForm dataForm = new DataForm(DataForm.Type.result);

        FormField.Builder desc = FormField.builder("muc#roominfo_description");
        desc.addValue("The place for all good witches!");
        dataForm.addField(desc.build());

        FormField.Builder subject = FormField.builder("muc#roominfo_subject");
        subject.addValue("Spells");
        dataForm.addField(subject.build());

        FormField.Builder occupants = FormField.builder("muc#roominfo_occupants");
        occupants.addValue("3");
        dataForm.addField(occupants.build());

        DiscoverInfo discoInfo = new DiscoverInfo();
        discoInfo.addExtension(dataForm);
        RoomInfo roomInfo = new RoomInfo(discoInfo);
        assertEquals("The place for all good witches!", roomInfo.getDescription());
        assertEquals("Spells", roomInfo.getSubject());
        assertEquals(3, roomInfo.getOccupantsCount());
    }
}
