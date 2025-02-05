/**
 *
 * Copyright 2014 Anno van Vliet
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
package com.advisoryapps.smackx.xdata.packet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.advisoryapps.smack.packet.Element;
import com.advisoryapps.smack.test.util.SmackTestSuite;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;

import com.advisoryapps.smackx.xdata.FormField;
import com.advisoryapps.smackx.xdata.FormField.Type;
import com.advisoryapps.smackx.xdata.provider.DataFormProvider;
import com.advisoryapps.smackx.xdatalayout.packet.DataLayout;
import com.advisoryapps.smackx.xdatalayout.packet.DataLayout.Fieldref;
import com.advisoryapps.smackx.xdatalayout.packet.DataLayout.Section;
import com.advisoryapps.smackx.xdatalayout.packet.DataLayout.Text;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement.RangeValidateElement;

import org.junit.Test;

/**
 * Unit tests for DataForm reading and parsing.
 *
 * @author Anno van Vliet
 *
 */
public class DataFormTest extends SmackTestSuite {
    private static final String TEST_OUTPUT_1 = "<x xmlns='jabber:x:data' type='submit'><instructions>InstructionTest1</instructions><field var='testField1'/></x>";
    private static final String TEST_OUTPUT_2 = "<x xmlns='jabber:x:data' type='submit'><instructions>InstructionTest1</instructions><field var='testField1'/><page xmlns='http://jabber.org/protocol/xdata-layout' label='Label'><fieldref var='testField1'/><section label='section Label'><text>SectionText</text></section><text>PageText</text></page></x>";
    private static final String TEST_OUTPUT_3 = "<x xmlns='jabber:x:data' type='submit'><instructions>InstructionTest1</instructions><field var='testField1'><validate xmlns='http://jabber.org/protocol/xdata-validate' datatype='xs:integer'><range min='1111' max='9999'/></validate></field></x>";

    private static final DataFormProvider pr = new DataFormProvider();

    @Test
    public void test() throws Exception {
        // Build a Form.
        DataForm df = new DataForm(DataForm.Type.submit);
        String instruction = "InstructionTest1";
        df.addInstruction(instruction);
        FormField field = FormField.builder("testField1").build();
        df.addField(field);

        assertNotNull(df.toXML());
        String output = df.toXML().toString();
        assertEquals(TEST_OUTPUT_1, output);

        XmlPullParser parser = PacketParserUtils.getParserFor(output);

        df = pr.parse(parser);

        assertNotNull(df);
        assertNotNull(df.getFields());
        assertEquals(1 , df.getFields().size());
        assertEquals(1 , df.getInstructions().size());

        assertNotNull(df.toXML());
        output = df.toXML().toString();
        assertEquals(TEST_OUTPUT_1, output);
    }

    @Test
    public void testLayout() throws Exception {
        // Build a Form.
        DataForm df = new DataForm(DataForm.Type.submit);
        String instruction = "InstructionTest1";
        df.addInstruction(instruction);
        FormField field = FormField.builder("testField1").build();
        df.addField(field);

        DataLayout layout = new DataLayout("Label");
        Fieldref reffield = new Fieldref("testField1");
        layout.getPageLayout().add(reffield);
        Section section = new Section("section Label");
        section.getSectionLayout().add(new Text("SectionText"));
        layout.getPageLayout().add(section);
        layout.getPageLayout().add(new Text("PageText"));

        df.addExtensionElement(layout);


        assertNotNull(df.toXML());
        String output = df.toXML().toString();
        assertEquals(TEST_OUTPUT_2, output);

        XmlPullParser parser = PacketParserUtils.getParserFor(output);

        df = pr.parse(parser);

        assertNotNull(df);
        assertNotNull(df.getExtensionElements());
        assertEquals(1 , df.getExtensionElements().size());
        Element element = df.getExtensionElements().get(0);
        assertNotNull(element);
        layout = (DataLayout) element;

        assertEquals(3 , layout.getPageLayout().size());

        assertNotNull(df.toXML());
        output = df.toXML().toString();
        assertEquals(TEST_OUTPUT_2, output);
    }

    @Test
    public void testValidation() throws Exception {
        // Build a Form.
        DataForm df = new DataForm(DataForm.Type.submit);
        String instruction = "InstructionTest1";
        df.addInstruction(instruction);
        FormField.Builder fieldBuilder = FormField.builder("testField1");

        ValidateElement dv = new RangeValidateElement("xs:integer", "1111", "9999");
        fieldBuilder.addFormFieldChildElement(dv);

        df.addField(fieldBuilder.build());

        assertNotNull(df.toXML());
        String output = df.toXML().toString();
        assertEquals(TEST_OUTPUT_3, output);

        XmlPullParser parser = PacketParserUtils.getParserFor(output);

        df = pr.parse(parser);

        assertNotNull(df);
        assertNotNull(df.getFields());
        assertEquals(1 , df.getFields().size());
        Element element = ValidateElement.from(df.getFields().get(0));
        assertNotNull(element);
        dv = (ValidateElement) element;

        assertEquals("xs:integer" , dv.getDatatype());

        assertNotNull(df.toXML());
        output = df.toXML().toString();
        assertEquals(TEST_OUTPUT_3, output);
    }

    @Test
    public void testFixedField() throws Exception {
        final String formWithFixedField = "<x xmlns='jabber:x:data' type='submit'><instructions>InstructionTest1</instructions><field type='fixed'></field></x>";
        DataForm df = pr.parse(PacketParserUtils.getParserFor(formWithFixedField));
        assertEquals(Type.fixed, df.getFields().get(0).getType());
    }
}
