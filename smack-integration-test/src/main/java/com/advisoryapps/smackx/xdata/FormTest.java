/**
 *
 * Copyright 2004 Jive Software, 2017-2019 Florian Schmaus.
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

package com.advisoryapps.smackx.xdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.StanzaCollector;
import com.advisoryapps.smack.filter.ThreadFilter;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smackx.xdata.FormField.Type;
import com.advisoryapps.smackx.xdata.packet.DataForm;

import org.igniterealtime.smack.inttest.AbstractSmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTestEnvironment;

/**
 * Tests the DataForms extensions.
 *
 * @author Gaston Dombiak
 */
public class FormTest extends AbstractSmackIntegrationTest {

    public FormTest(SmackIntegrationTestEnvironment<?> environment) {
        super(environment);
    }

    /**
     * 1. Create a form to fill out and send it to the other user
     * 2. Retrieve the form to fill out, complete it and return it to the requestor
     * 3. Retrieve the completed form and check that everything is OK
     *
     * @throws InterruptedException if the calling thread was interrupted.
     * @throws NotConnectedException if the XMPP connection is not connected.
     */
    @SuppressWarnings("deprecation")
    @SmackIntegrationTest
    public void testFilloutForm() throws NotConnectedException, InterruptedException {
        Form formToSend = new Form(DataForm.Type.form);
        formToSend.setInstructions(
            "Fill out this form to report your case.\nThe case will be created automatically.");
        formToSend.setTitle("Case configurations");
        // Add a hidden variable
        FormField.Builder field = FormField.builder("hidden_var");
        field.setType(FormField.Type.hidden);
        field.addValue("Some value for the hidden variable");
        formToSend.addField(field.build());
        // Add a fixed variable
        field = FormField.builder();
        field.addValue("Section 1: Case description");
        field.setType(Type.fixed);
        formToSend.addField(field.build());
        // Add a text-single variable
        field = FormField.builder("name");
        field.setLabel("Enter a name for the case");
        field.setType(FormField.Type.text_single);
        formToSend.addField(field.build());
        // Add a text-multi variable
        field = FormField.builder("description");
        field.setLabel("Enter a description");
        field.setType(FormField.Type.text_multi);
        formToSend.addField(field.build());
        // Add a boolean variable
        field = FormField.builder("time");
        field.setLabel("Is this your first case?");
        field.setType(FormField.Type.bool);
        formToSend.addField(field.build());
        // Add a text variable where an int value is expected
        field = FormField.builder("age");
        field.setLabel("How old are you?");
        field.setType(FormField.Type.text_single);
        formToSend.addField(field.build());

        // Create the chats between the two participants
        com.advisoryapps.smack.chat.Chat chat = com.advisoryapps.smack.chat.ChatManager.getInstanceFor(conOne).createChat(conTwo.getUser(), null);
        StanzaCollector collector = conOne.createStanzaCollector(
                new ThreadFilter(chat.getThreadID()));
        StanzaCollector collector2 = conTwo.createStanzaCollector(
                new ThreadFilter(chat.getThreadID()));

        Message msg = new Message();
        msg.setBody("To enter a case please fill out this form and send it back to me");
        msg.addExtension(formToSend.getDataFormToSend());

        try {
            // Send the message with the form to fill out
            chat.sendMessage(msg);

            // Get the message with the form to fill out
            Message msg2 = collector2.nextResult();
            assertNotNull("Message not found", msg2);
            // Retrieve the form to fill out
            Form formToRespond = Form.getFormFrom(msg2);
            assertNotNull(formToRespond);
            assertNotNull(formToRespond.getField("name"));
            assertNotNull(formToRespond.getField("description"));
            // Obtain the form to send with the replies
            Form completedForm = formToRespond.createAnswerForm();
            assertNotNull(completedForm.getField("hidden_var"));
            // Check that a field of type String does not accept booleans
            try {
                completedForm.setAnswer("name", true);
                fail("A boolean value was set to a field of type String");
            }
            catch (IllegalArgumentException e) {
                // This exception is expected.
            }
            completedForm.setAnswer("name", "Credit card number invalid");
            completedForm.setAnswer(
                "description",
                "The ATM says that my credit card number is invalid. What's going on?");
            completedForm.setAnswer("time", true);
            completedForm.setAnswer("age", 20);
            // Create a new message to send with the completed form
            msg2 = new Message();
            msg2.setTo(conOne.getUser().asBareJid());
            msg2.setThread(msg.getThread());
            msg2.setType(Message.Type.chat);
            msg2.setBody("To enter a case please fill out this form and send it back to me");
            // Add the completed form to the message
            msg2.addExtension(completedForm.getDataFormToSend());
            // Send the message with the completed form
            conTwo.sendStanza(msg2);

            // Get the message with the completed form
            Message msg3 = collector.nextResult();
            assertNotNull("Message not found", msg3);
            // Retrieve the completed form
            completedForm = Form.getFormFrom(msg3);
            assertNotNull(completedForm);
            assertNotNull(completedForm.getField("name"));
            assertNotNull(completedForm.getField("description"));
            assertEquals(
                 completedForm.getField("name").getValues().get(0).toString(),
                "Credit card number invalid");
            assertNotNull(completedForm.getField("time"));
            assertNotNull(completedForm.getField("age"));
            assertEquals("The age is bad", "20", completedForm.getField("age").getValues().get(0).toString());

        }
        finally {
            collector.cancel();
            collector2.cancel();
        }
    }

}
