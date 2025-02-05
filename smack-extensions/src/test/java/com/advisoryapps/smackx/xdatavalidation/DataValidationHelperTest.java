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
package com.advisoryapps.smackx.xdatavalidation;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.advisoryapps.smackx.xdata.FormField;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement.BasicValidateElement;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement.ListRange;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement.OpenValidateElement;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement.RangeValidateElement;
import com.advisoryapps.smackx.xdatavalidation.packet.ValidateElement.RegexValidateElement;

import org.junit.jupiter.api.Test;

/**
 * Data validation helper test.
 * @author Anno van Vliet
 *
 */
public class DataValidationHelperTest {

    @Test
    public void testCheckConsistencyFormFieldBasicValidateElement() {
        FormField.Builder field = FormField.builder("var")
                        .setType(FormField.Type.jid_single);
        BasicValidateElement element = new BasicValidateElement(null);
        ValidationConsistencyException vce = assertThrows(ValidationConsistencyException.class,
                        () -> element.checkConsistency(field));
        assertEquals("Field type 'jid-single' is not consistent with validation method 'basic'.", vce.getMessage());

        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                        () -> new ListRange(-1L, 1L));
        assertEquals("unsigned 32-bit integers can't be negative", iae.getMessage());

        element.setListRange(new ListRange(10L, 100L));
        vce = assertThrows(ValidationConsistencyException.class, () -> element.checkConsistency(field));
        assertEquals("Field type is not of type 'list-multi' while a 'list-range' is defined.", vce.getMessage());

        FormField.Builder fieldListMulti = field.setType(FormField.Type.list_multi);
        element.checkConsistency(fieldListMulti);
    }


    @Test
    public void testCheckConsistencyFormFieldOpenValidateElement() {
        FormField.Builder field = FormField.builder("var")
                        .setType(FormField.Type.hidden);
        OpenValidateElement element = new OpenValidateElement(null);
        ValidationConsistencyException e = assertThrows(ValidationConsistencyException.class,
                        () -> element.checkConsistency(field));
        assertEquals("Field type 'hidden' is not consistent with validation method 'open'.", e.getMessage());
    }

    @Test
    public void testCheckConsistencyFormFieldRangeValidateElement() {
        FormField.Builder field = FormField.builder("var")
                        .setType(FormField.Type.text_multi);
        RangeValidateElement element = new RangeValidateElement("xs:integer", null,  "99");
        ValidationConsistencyException e = assertThrows(ValidationConsistencyException.class,
                        () -> element.checkConsistency(field));
        assertEquals("Field type 'text-multi' is not consistent with validation method 'range'.", e.getMessage());
    }

    @Test
    public void testCheckConsistencyFormFieldRegexValidateElement() {
        FormField.Builder field = FormField.builder("var")
                        .setType(FormField.Type.list_multi);
        RegexValidateElement element = new RegexValidateElement(null, ".*");
        ValidationConsistencyException e = assertThrows(ValidationConsistencyException.class,
                        () -> element.checkConsistency(field));
        assertEquals("Field type 'list-multi' is not consistent with validation method 'regex'.", e.getMessage());
    }
}
