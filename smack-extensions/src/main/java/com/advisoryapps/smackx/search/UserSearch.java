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
package com.advisoryapps.smackx.search;

import java.io.IOException;

import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.SimpleIQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.parsing.SmackParsingException;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.PacketParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import com.advisoryapps.smackx.xdata.Form;
import com.advisoryapps.smackx.xdata.FormField;
import com.advisoryapps.smackx.xdata.packet.DataForm;

import org.jxmpp.jid.DomainBareJid;

/**
 * Implements the protocol currently used to search information repositories on the Jabber network. To date, the jabber:iq:search protocol
 * has been used mainly to search for people who have registered with user directories (e.g., the "Jabber User Directory" hosted at users.jabber.org).
 * However, the jabber:iq:search protocol is not limited to user directories, and could be used to search other Jabber information repositories
 * (such as chatroom directories) or even to provide a Jabber interface to conventional search engines.
 *
 * The basic functionality is to query an information repository regarding the possible search fields, to send a search query, and to receive search results.
 *
 * @author Derek DeMoro
 */
public class UserSearch extends SimpleIQ {

    public static final String ELEMENT = QUERY_ELEMENT;
    public static final String NAMESPACE = "jabber:iq:search";

    /**
     * Creates a new instance of UserSearch.
     */
    public UserSearch() {
        super(ELEMENT, NAMESPACE);
    }

    /**
     * Returns the form for all search fields supported by the search service.
     *
     * @param con           the current XMPPConnection.
     * @param searchService the search service to use. (ex. search.jivesoftware.com)
     * @return the search form received by the server.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public Form getSearchForm(XMPPConnection con, DomainBareJid searchService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        UserSearch search = new UserSearch();
        search.setType(IQ.Type.get);
        search.setTo(searchService);

        IQ response = con.createStanzaCollectorAndSend(search).nextResultOrThrow();
        return Form.getFormFrom(response);
    }

    /**
     * Sends the filled out answer form to be sent and queried by the search service.
     *
     * @param con           the current XMPPConnection.
     * @param searchForm    the <code>Form</code> to send for querying.
     * @param searchService the search service to use. (ex. search.jivesoftware.com)
     * @return ReportedData the data found from the query.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public ReportedData sendSearchForm(XMPPConnection con, Form searchForm, DomainBareJid searchService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        UserSearch search = new UserSearch();
        search.setType(IQ.Type.set);
        search.setTo(searchService);
        search.addExtension(searchForm.getDataFormToSend());

        IQ response = con.createStanzaCollectorAndSend(search).nextResultOrThrow();
        return ReportedData.getReportedDataFrom(response);
    }

    /**
     * Sends the filled out answer form to be sent and queried by the search service.
     *
     * @param con           the current XMPPConnection.
     * @param searchForm    the <code>Form</code> to send for querying.
     * @param searchService the search service to use. (ex. search.jivesoftware.com)
     * @return ReportedData the data found from the query.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public ReportedData sendSimpleSearchForm(XMPPConnection con, Form searchForm, DomainBareJid searchService) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        SimpleUserSearch search = new SimpleUserSearch();
        search.setForm(searchForm);
        search.setType(IQ.Type.set);
        search.setTo(searchService);

        SimpleUserSearch response = con.createStanzaCollectorAndSend(search).nextResultOrThrow();
        return response.getReportedData();
    }

    /**
     * Internal Search service Provider.
     */
    public static class Provider extends IQProvider<IQ> {

        // FIXME this provider does return two different types of IQs
        @Override
        public IQ parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
            UserSearch search = null;
            SimpleUserSearch simpleUserSearch = new SimpleUserSearch();

            boolean done = false;
            while (!done) {
                XmlPullParser.Event eventType = parser.next();
                if (eventType == XmlPullParser.Event.START_ELEMENT && parser.getName().equals("instructions")) {
                    buildDataForm(simpleUserSearch, parser.nextText(), parser, xmlEnvironment);
                    return simpleUserSearch;
                }
                else if (eventType == XmlPullParser.Event.START_ELEMENT && parser.getName().equals("item")) {
                    simpleUserSearch.parseItems(parser);
                    return simpleUserSearch;
                }
                else if (eventType == XmlPullParser.Event.START_ELEMENT && parser.getNamespace().equals("jabber:x:data")) {
                    // Otherwise, it must be a packet extension.
                    search = new UserSearch();
                    PacketParserUtils.addExtensionElement(search, parser, xmlEnvironment);
                }
                else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                    if (parser.getName().equals("query")) {
                        done = true;
                    }
                }
            }

            if (search != null) {
                return search;
            }
            return simpleUserSearch;
        }
    }

    private static void buildDataForm(SimpleUserSearch search,
                    String instructions, XmlPullParser parser, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException, SmackParsingException {
        DataForm dataForm = new DataForm(DataForm.Type.form);
        boolean done = false;
        dataForm.setTitle("User Search");
        dataForm.addInstruction(instructions);
        while (!done) {
            XmlPullParser.Event eventType = parser.next();

            if (eventType == XmlPullParser.Event.START_ELEMENT && !parser.getNamespace().equals("jabber:x:data")) {
                String name = parser.getName();
                FormField.Builder field = FormField.builder(name);

                // Handle hard coded values.
                if (name.equals("first")) {
                    field.setLabel("First Name");
                }
                else if (name.equals("last")) {
                    field.setLabel("Last Name");
                }
                else if (name.equals("email")) {
                    field.setLabel("Email Address");
                }
                else if (name.equals("nick")) {
                    field.setLabel("Nickname");
                }

                field.setType(FormField.Type.text_single);
                dataForm.addField(field.build());
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getName().equals("query")) {
                    done = true;
                }
            }
            else if (eventType == XmlPullParser.Event.START_ELEMENT && parser.getNamespace().equals("jabber:x:data")) {
                PacketParserUtils.addExtensionElement(search, parser, xmlEnvironment);
                done = true;
            }
        }
        if (search.getExtension("x", "jabber:x:data") == null) {
            search.addExtension(dataForm);
        }
    }


}
