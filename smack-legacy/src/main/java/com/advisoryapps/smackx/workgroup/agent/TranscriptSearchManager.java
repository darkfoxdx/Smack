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

package com.advisoryapps.smackx.workgroup.agent;

import com.advisoryapps.smack.SmackException.NoResponseException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;
import com.advisoryapps.smack.packet.IQ;

import com.advisoryapps.smackx.search.ReportedData;
import com.advisoryapps.smackx.workgroup.packet.TranscriptSearch;
import com.advisoryapps.smackx.xdata.Form;

import org.jxmpp.jid.DomainBareJid;

/**
 * A TranscriptSearchManager helps to retrieve the form to use for searching transcripts
 * {@link #getSearchForm(DomainBareJid)} or to submit a search form and return the results of
 * the search {@link #submitSearch(DomainBareJid, Form)}.
 *
 * @author Gaston Dombiak
 */
public class TranscriptSearchManager {
    private final XMPPConnection connection;

    public TranscriptSearchManager(XMPPConnection connection) {
        this.connection = connection;
    }

    /**
     * Returns the Form to use for searching transcripts. It is unlikely that the server
     * will change the form (without a restart) so it is safe to keep the returned form
     * for future submissions.
     *
     * @param serviceJID the address of the workgroup service.
     * @return the Form to use for searching transcripts.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public Form getSearchForm(DomainBareJid serviceJID) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException  {
        TranscriptSearch search = new TranscriptSearch();
        search.setType(IQ.Type.get);
        search.setTo(serviceJID);

        TranscriptSearch response = connection.createStanzaCollectorAndSend(
                        search).nextResultOrThrow();
        return Form.getFormFrom(response);
    }

    /**
     * Submits the completed form and returns the result of the transcript search. The result
     * will include all the data returned from the server so be careful with the amount of
     * data that the search may return.
     *
     * @param serviceJID    the address of the workgroup service.
     * @param completedForm the filled out search form.
     * @return the result of the transcript search.
     * @throws XMPPErrorException if there was an XMPP error returned.
     * @throws NoResponseException if there was no response from the remote entity.
     * @throws NotConnectedException if the XMPP connection is not connected.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    public ReportedData submitSearch(DomainBareJid serviceJID, Form completedForm) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        TranscriptSearch search = new TranscriptSearch();
        search.setType(IQ.Type.get);
        search.setTo(serviceJID);
        search.addExtension(completedForm.getDataFormToSend());

        TranscriptSearch response = connection.createStanzaCollectorAndSend(
                        search).nextResultOrThrow();
        return ReportedData.getReportedDataFrom(response);
    }
}


