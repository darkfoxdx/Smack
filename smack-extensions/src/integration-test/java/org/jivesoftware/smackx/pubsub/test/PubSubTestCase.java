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
package com.advisoryapps.smackx.pubsub.test;

import com.advisoryapps.smack.XMPPException;
import com.advisoryapps.smack.test.SmackTestCase;
import com.advisoryapps.smackx.pubsub.AccessModel;
import com.advisoryapps.smackx.pubsub.ConfigureForm;
import com.advisoryapps.smackx.pubsub.FormType;
import com.advisoryapps.smackx.pubsub.LeafNode;
import com.advisoryapps.smackx.pubsub.PubSubManager;

/**
 *
 * @author Robin Collier
 *
 */
public abstract class PubSubTestCase extends SmackTestCase
{
	private PubSubManager[] manager;

	public PubSubTestCase(String arg0)
	{
		super(arg0);
	}

	public PubSubTestCase()
	{
		super("PubSub Test Case");
	}

	protected LeafNode getRandomPubnode(PubSubManager pubMgr, boolean persistItems, boolean deliverPayload) throws XMPPException
	{
		ConfigureForm form = new ConfigureForm(FormType.submit);
		form.setPersistentItems(persistItems);
		form.setDeliverPayloads(deliverPayload);
		form.setAccessModel(AccessModel.open);
		return (LeafNode)pubMgr.createNode("/test/Pubnode" + System.currentTimeMillis(), form);
	}

	protected LeafNode getPubnode(PubSubManager pubMgr, boolean persistItems, boolean deliverPayload, String nodeId) throws XMPPException
	{
		LeafNode node = null;

		try
		{
			node = (LeafNode)pubMgr.getNode(nodeId);
		}
		catch (XMPPException e)
		{
			ConfigureForm form = new ConfigureForm(FormType.submit);
			form.setPersistentItems(persistItems);
			form.setDeliverPayloads(deliverPayload);
			form.setAccessModel(AccessModel.open);
			node = (LeafNode)pubMgr.createNode(nodeId, form);
		}
		return node;
	}

	protected PubSubManager getManager(int idx)
	{
		if (manager == null)
		{
			manager = new PubSubManager[getMaxConnections()];

			for(int i=0; i<manager.length; i++)
			{
				manager[i] = new PubSubManager(getConnection(i), getService());
			}
		}
		return manager[idx];
	}

	protected String getService()
	{
		return "pubsub." + getXMPPServiceDomain();
	}
}
