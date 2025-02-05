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
package com.advisoryapps.smackx.pubsub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import com.advisoryapps.smack.SmackConfiguration;
import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.ThreadedDummyConnection;
import com.advisoryapps.smack.XMPPException;
import com.advisoryapps.smack.XMPPException.XMPPErrorException;
import com.advisoryapps.smack.packet.IQ.Type;
import com.advisoryapps.smack.packet.StanzaError;
import com.advisoryapps.smack.packet.StanzaError.Condition;

import com.advisoryapps.smackx.InitExtensions;
import com.advisoryapps.smackx.disco.packet.DiscoverInfo;
import com.advisoryapps.smackx.disco.packet.DiscoverInfo.Identity;
import com.advisoryapps.smackx.pubsub.packet.PubSub;
import com.advisoryapps.smackx.xdata.packet.DataForm;

import org.junit.Test;

/**
 * Configure form test.
 * @author Robin Collier
 *
 */
public class ConfigureFormTest extends InitExtensions {
    @Test
    public void checkChildrenAssocPolicy() {
        ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
        form.setChildrenAssociationPolicy(ChildrenAssociationPolicy.owners);
        assertEquals(ChildrenAssociationPolicy.owners, form.getChildrenAssociationPolicy());
    }

    @Test
    public void getConfigFormWithInsufficientPrivileges() throws XMPPException, SmackException, IOException, InterruptedException {
        ThreadedDummyConnection con = ThreadedDummyConnection.newInstance();
        PubSubManager mgr = new PubSubManager(con, PubSubManagerTest.DUMMY_PUBSUB_SERVICE);
        DiscoverInfo info = new DiscoverInfo();
        info.setType(Type.result);
        info.setFrom(PubSubManagerTest.DUMMY_PUBSUB_SERVICE);
        Identity ident = new Identity("pubsub", null, "leaf");
        info.addIdentity(ident);
        con.addIQReply(info);

        Node node = mgr.getNode("princely_musings");

        PubSub errorIq = new PubSub();
        errorIq.setType(Type.error);
        errorIq.setFrom(PubSubManagerTest.DUMMY_PUBSUB_SERVICE);
        StanzaError.Builder error = StanzaError.getBuilder(Condition.forbidden);
        errorIq.setError(error);
        con.addIQReply(errorIq);

        try {
            node.getNodeConfiguration();
            fail();
        }
        catch (XMPPErrorException e) {
            assertEquals(StanzaError.Type.AUTH, e.getStanzaError().getType());
        }
    }

    @Test(expected = SmackException.class)
    public void getConfigFormWithTimeout() throws XMPPException, SmackException, InterruptedException {
        ThreadedDummyConnection con = new ThreadedDummyConnection();
        PubSubManager mgr = new PubSubManager(con, PubSubManagerTest.DUMMY_PUBSUB_SERVICE);
        DiscoverInfo info = new DiscoverInfo();
        Identity ident = new Identity("pubsub", null, "leaf");
        info.addIdentity(ident);
        con.addIQReply(info);

        Node node = mgr.getNode("princely_musings");

        SmackConfiguration.setDefaultReplyTimeout(100);
        con.setTimeout();

        node.getNodeConfiguration();
    }

    @Test
    public void checkNotificationType() {
        ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
        form.setNotificationType(NotificationType.normal);
        assertEquals(NotificationType.normal, form.getNotificationType());
        form.setNotificationType(NotificationType.headline);
        assertEquals(NotificationType.headline, form.getNotificationType());
    }

}
