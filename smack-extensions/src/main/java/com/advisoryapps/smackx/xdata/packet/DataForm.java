/**
 *
 * Copyright 2003-2007 Jive Software.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.advisoryapps.smack.packet.Element;
import com.advisoryapps.smack.packet.ExtensionElement;
import com.advisoryapps.smack.packet.Stanza;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.util.XmlStringBuilder;

import com.advisoryapps.smackx.xdata.FormField;

/**
 * Represents a form that could be use for gathering data as well as for reporting data
 * returned from a search.
 *
 * @author Gaston Dombiak
 */
public class DataForm implements ExtensionElement {

    public static final String NAMESPACE = "jabber:x:data";
    public static final String ELEMENT = "x";

    public enum Type {
        /**
         * This stanza contains a form to fill out. Display it to the user (if your program can).
         */
        form,

        /**
         * The form is filled out, and this is the data that is being returned from the form.
         */
        submit,

        /**
         * The form was cancelled. Tell the asker that piece of information.
         */
        cancel,

        /**
         * Data results being returned from a search, or some other query.
         */
        result,
        ;

        public static Type fromString(String string) {
            return Type.valueOf(string.toLowerCase(Locale.US));
        }
    }

    private Type type;
    private String title;
    private final List<String> instructions = new ArrayList<>();
    private ReportedData reportedData;
    private final List<Item> items = new ArrayList<>();
    private final Map<String, FormField> fields = new LinkedHashMap<>();
    private final List<Element> extensionElements = new ArrayList<>();

    public DataForm(Type type) {
        this.type = type;
    }

    /**
     * Returns the meaning of the data within the context. The data could be part of a form
     * to fill out, a form submission or data results.
     *
     * @return the form's type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the description of the data. It is similar to the title on a web page or an X
     * window.  You can put a &lt;title/&gt; on either a form to fill out, or a set of data results.
     *
     * @return description of the data.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns a List of the list of instructions that explain how to fill out the form and
     * what the form is about. The dataform could include multiple instructions since each
     * instruction could not contain newlines characters. Join the instructions together in order
     * to show them to the user.
     *
     * @return a List of the list of instructions that explain how to fill out the form.
     */
    public List<String> getInstructions() {
        synchronized (instructions) {
            return Collections.unmodifiableList(new ArrayList<>(instructions));
        }
    }

    /**
     * Returns the fields that will be returned from a search.
     *
     * @return fields that will be returned from a search.
     */
    public ReportedData getReportedData() {
        return reportedData;
    }

    /**
     * Returns a List of the items returned from a search.
     *
     * @return a List of the items returned from a search.
     */
    public List<Item> getItems() {
        synchronized (items) {
            return Collections.unmodifiableList(new ArrayList<>(items));
        }
    }

    /**
     * Returns a List of the fields that are part of the form.
     *
     * @return a List of the fields that are part of the form.
     */
    public List<FormField> getFields() {
        synchronized (fields) {
            return new ArrayList<>(fields.values());
        }
    }

    /**
     * Return the form field with the given variable name or null.
     *
     * @param variableName TODO javadoc me please
     * @return the form field or null.
     * @since 4.1
     */
    public FormField getField(String variableName) {
        synchronized (fields) {
            return fields.get(variableName);
        }
    }

    public FormField replaceField(FormField field) {
        String fieldVariableName = field.getVariable();

        synchronized (fields) {
            if (!fields.containsKey(fieldVariableName)) {
                throw new IllegalArgumentException("No field with the name " + fieldVariableName + " exists");
            }
            return fields.put(fieldVariableName, field);
        }
    }

    /**
     * Check if a form field with the given variable name exists.
     *
     * @param variableName TODO javadoc me please
     * @return true if a form field with the variable name exists, false otherwise.
     * @since 4.2
     */
    public boolean hasField(String variableName) {
        synchronized (fields) {
            return fields.containsKey(variableName);
        }
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    /**
     * Sets the description of the data. It is similar to the title on a web page or an X window.
     * You can put a &lt;title/&gt; on either a form to fill out, or a set of data results.
     *
     * @param title description of the data.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the list of instructions that explain how to fill out the form and what the form is
     * about. The dataform could include multiple instructions since each instruction could not
     * contain newlines characters.
     *
     * @param instructions list of instructions that explain how to fill out the form.
     */
    public void setInstructions(List<String> instructions) {
        synchronized (this.instructions) {
            this.instructions.clear();
            this.instructions.addAll(instructions);
        }
    }

    /**
     * Sets the fields that will be returned from a search.
     *
     * @param reportedData the fields that will be returned from a search.
     */
    public void setReportedData(ReportedData reportedData) {
        this.reportedData = reportedData;
    }

    /**
     * Adds a new field as part of the form.
     *
     * @param field the field to add to the form.
     */
    public void addField(FormField field) {
        String fieldVariableName = field.getVariable();
        // Form field values must be unique unless they are of type 'fixed', in
        // which case their variable name may be 'null', and therefore could
        // appear multiple times within the same form.
        if (fieldVariableName != null && hasField(fieldVariableName)) {
            throw new IllegalArgumentException("This data form already contains a form field with the variable name '"
                            + fieldVariableName + "'");
        }
        synchronized (fields) {
            fields.put(fieldVariableName, field);
        }
    }

    /**
     * Add the given fields to this form.
     *
     * @param fieldsToAdd TODO javadoc me please
     * @return true if a field was overridden.
     * @since 4.3.0
     */
    public boolean addFields(Collection<FormField> fieldsToAdd) {
        boolean fieldOverridden = false;
        synchronized (fields) {
            for (FormField field : fieldsToAdd) {
                FormField previousField = fields.put(field.getVariable(), field);
                if (previousField != null) {
                    fieldOverridden = true;
                }
            }
        }
        return fieldOverridden;
    }

    /**
     * Adds a new instruction to the list of instructions that explain how to fill out the form
     * and what the form is about. The dataform could include multiple instructions since each
     * instruction could not contain newlines characters.
     *
     * @param instruction the new instruction that explain how to fill out the form.
     */
    public void addInstruction(String instruction) {
        synchronized (instructions) {
            instructions.add(instruction);
        }
    }

    /**
     * Adds a new item returned from a search.
     *
     * @param item the item returned from a search.
     */
    public void addItem(Item item) {
        synchronized (items) {
            items.add(item);
        }
    }

    public void addExtensionElement(Element element) {
        extensionElements.add(element);
    }

    public List<Element> getExtensionElements() {
        return Collections.unmodifiableList(extensionElements);
    }

    /**
     * Returns the hidden FORM_TYPE field or null if this data form has none.
     *
     * @return the hidden FORM_TYPE field or null.
     * @since 4.1
     */
    public FormField getHiddenFormTypeField() {
        FormField field = getField(FormField.FORM_TYPE);
        if (field != null && field.getType() == FormField.Type.hidden) {
            return field;
        }
        return null;
    }

    /**
     * Returns true if this DataForm has at least one FORM_TYPE field which is
     * hidden. This method is used for sanity checks.
     *
     * @return true if there is at least one field which is hidden.
     */
    public boolean hasHiddenFormTypeField() {
        return getHiddenFormTypeField() != null;
    }

    @Override
    public XmlStringBuilder toXML(XmlEnvironment xmlEnvironment) {
        XmlStringBuilder buf = new XmlStringBuilder(this, xmlEnvironment);
        buf.attribute("type", getType());
        buf.rightAngleBracket();

        buf.optElement("title", getTitle());
        for (String instruction : getInstructions()) {
            buf.element("instructions", instruction);
        }
        // Append the list of fields returned from a search
        if (getReportedData() != null) {
            buf.append(getReportedData().toXML());
        }
        // Loop through all the items returned from a search and append them to the string buffer
        for (Item item : getItems()) {
            buf.append(item.toXML());
        }
        // Add all form fields.
        buf.append(getFields());
        for (Element element : extensionElements) {
            buf.append(element.toXML());
        }
        buf.closeElement(this);
        return buf;
    }

    /**
     * Get data form from stanza.
     * @param packet TODO javadoc me please
     * @return the DataForm or null
     */
    public static DataForm from(Stanza packet) {
        return (DataForm) packet.getExtension(ELEMENT, NAMESPACE);
    }

    /**
     *
     * Represents the fields that will be returned from a search. This information is useful when
     * you try to use the jabber:iq:search namespace to return dynamic form information.
     *
     * @author Gaston Dombiak
     */
    public static class ReportedData {
        public static final String ELEMENT = "reported";

        private List<FormField> fields = new ArrayList<>();

        public ReportedData(List<FormField> fields) {
            this.fields = fields;
        }

        /**
         * Returns the fields returned from a search.
         *
         * @return the fields returned from a search.
         */
        public List<FormField> getFields() {
            return Collections.unmodifiableList(new ArrayList<>(fields));
        }

        public CharSequence toXML() {
            XmlStringBuilder buf = new XmlStringBuilder();
            buf.openElement(ELEMENT);
            // Loop through all the form items and append them to the string buffer
            for (FormField field : getFields()) {
                buf.append(field.toXML());
            }
            buf.closeElement(ELEMENT);
            return buf;
        }
    }

    /**
     *
     * Represents items of reported data.
     *
     * @author Gaston Dombiak
     */
    public static class Item {
        public static final String ELEMENT = "item";

        private List<FormField> fields = new ArrayList<>();

        public Item(List<FormField> fields) {
            this.fields = fields;
        }

        /**
         * Returns the fields that define the data that goes with the item.
         *
         * @return the fields that define the data that goes with the item.
         */
        public List<FormField> getFields() {
            return Collections.unmodifiableList(new ArrayList<>(fields));
        }

        public CharSequence toXML() {
            XmlStringBuilder buf = new XmlStringBuilder();
            buf.openElement(ELEMENT);
            // Loop through all the form items and append them to the string buffer
            for (FormField field : getFields()) {
                buf.append(field.toXML());
            }
            buf.closeElement(ELEMENT);
            return buf;
        }
    }
}
