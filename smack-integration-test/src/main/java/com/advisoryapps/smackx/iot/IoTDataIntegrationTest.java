/**
 *
 * Copyright 2016-2019 Florian Schmaus
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
package com.advisoryapps.smackx.iot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.advisoryapps.smack.util.StringUtils;

import com.advisoryapps.smackx.iot.data.IoTDataManager;
import com.advisoryapps.smackx.iot.data.ThingMomentaryReadOutRequest;
import com.advisoryapps.smackx.iot.data.ThingMomentaryReadOutResult;
import com.advisoryapps.smackx.iot.data.element.IoTDataField;
import com.advisoryapps.smackx.iot.data.element.IoTDataField.IntField;
import com.advisoryapps.smackx.iot.data.element.IoTFieldsExtension;
import com.advisoryapps.smackx.iot.data.element.NodeElement;
import com.advisoryapps.smackx.iot.data.element.TimestampElement;

import org.igniterealtime.smack.inttest.AbstractSmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTest;
import org.igniterealtime.smack.inttest.SmackIntegrationTestEnvironment;
import org.igniterealtime.smack.inttest.util.IntegrationTestRosterUtil;

public class IoTDataIntegrationTest extends AbstractSmackIntegrationTest {

    private final IoTDataManager iotDataManagerOne;

    private final IoTDataManager iotDataManagerTwo;

    public IoTDataIntegrationTest(SmackIntegrationTestEnvironment<?> environment) {
        super(environment);
        iotDataManagerOne = IoTDataManager.getInstanceFor(conOne);
        iotDataManagerTwo = IoTDataManager.getInstanceFor(conTwo);
    }

    /**
     * Connection one provides a thing, which momentary value is read out by connection two.
     *
     * @throws Exception if an exception occurs.
     * @throws TimeoutException if there was a timeout.
     */
    @SmackIntegrationTest
    public void dataTest() throws Exception {
        final String key = StringUtils.randomString(12);
        final String sn = StringUtils.randomString(12);
        final int value = INSECURE_RANDOM.nextInt();

        Thing dataThing = Thing.builder().setKey(key).setSerialNumber(sn).setMomentaryReadOutRequestHandler(new ThingMomentaryReadOutRequest() {
            @Override
            public void momentaryReadOutRequest(ThingMomentaryReadOutResult callback) {
                IoTDataField.IntField field = new IntField(testRunId, value);
                callback.momentaryReadOut(Collections.singletonList(field));
            }
        }).build();

        iotDataManagerOne.installThing(dataThing);

        List<IoTFieldsExtension> values;
        try {
            IntegrationTestRosterUtil.ensureBothAccountsAreSubscribedToEachOther(conOne, conTwo, timeout);

            values = iotDataManagerTwo.requestMomentaryValuesReadOut(conOne.getUser());
        }
        finally {
            iotDataManagerOne.uninstallThing(dataThing);
            IntegrationTestRosterUtil.ensureBothAccountsAreNotInEachOthersRoster(conOne, conTwo);
        }

        assertEquals(1, values.size());
        IoTFieldsExtension iotFieldsExtension = values.get(0);
        List<NodeElement> nodes = iotFieldsExtension.getNodes();

        assertEquals(1, nodes.size());
        NodeElement node = nodes.get(0);
        List<TimestampElement> timestamps = node.getTimestampElements();

        assertEquals(1, timestamps.size());
        TimestampElement timestamp = timestamps.get(0);
        List<? extends IoTDataField> fields = timestamp.getDataFields();

        assertEquals(1, fields.size());
        IoTDataField dataField = fields.get(0);
        assertTrue(dataField instanceof IoTDataField.IntField);
        IoTDataField.IntField intDataField = (IoTDataField.IntField) dataField;
        assertEquals(testRunId, intDataField.getName());
        assertEquals(value, intDataField.getValue());
    }
}
