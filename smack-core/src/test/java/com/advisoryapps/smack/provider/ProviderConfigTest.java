/**
 *
 * Copyright the original author or authors
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
package com.advisoryapps.smack.provider;

import java.util.ArrayList;
import java.util.Collection;

import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.util.FileUtils;
import com.advisoryapps.smack.xml.XmlPullParser;

import org.junit.Assert;
import org.junit.Test;

public class ProviderConfigTest {

    @Test
    public void addGenericLoaderProvider() {
        ProviderManager.addLoader(new ProviderLoader() {

            @Override
            public Collection<IQProviderInfo> getIQProviderInfo() {
                ArrayList<IQProviderInfo> l = new ArrayList<IQProviderInfo>(1);
                l.add(new IQProviderInfo("provider", "test:provider",  new TestIQProvider()));
                return l;
            }

            @Override
            public Collection<ExtensionProviderInfo> getExtensionProviderInfo() {
                return null;
            }

            @Override
            public Collection<StreamFeatureProviderInfo> getStreamFeatureProviderInfo() {
                return null;
            }
        });

        Assert.assertNotNull(ProviderManager.getIQProvider("provider", "test:provider"));
    }

    @Test
    public void addClasspathFileLoaderProvider() throws Exception {
        ProviderManager.addLoader(new ProviderFileLoader(FileUtils.getStreamForClasspathFile("test.providers", null)));
        Assert.assertNotNull(ProviderManager.getIQProvider("provider", "test:file_provider"));
    }

    public static class TestIQProvider extends IQProvider<IQ> {

        @Override
        public IQ parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) {
            return null;
        }

    }
}
