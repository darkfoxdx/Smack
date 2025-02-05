/**
 *
 * Copyright 2014-2018 Florian Schmaus
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
package com.advisoryapps.smack.java7;

import java.util.List;

import com.advisoryapps.smack.SmackConfiguration;
import com.advisoryapps.smack.initializer.SmackInitializer;
import com.advisoryapps.smack.util.SystemUtil;
import com.advisoryapps.smack.util.stringencoder.Base64;
import com.advisoryapps.smack.util.stringencoder.Base64UrlSafeEncoder;
import com.advisoryapps.smack.util.stringencoder.java7.Java7Base64Encoder;
import com.advisoryapps.smack.util.stringencoder.java7.Java7Base64UrlSafeEncoder;

public class Java7SmackInitializer implements SmackInitializer {

    @Override
    public List<Exception> initialize() {
        if (SystemUtil.onAndroid()) {
            // @formatter:off
            throw new RuntimeException(
                            "You need to remove the smack-java7 dependency/jar from your build, " +
                            "as it does not run on Android. " +
                            "Use smack-android instead.");
            // @formatter:on
        }
        SmackConfiguration.setDefaultHostnameVerifier(new XmppHostnameVerifier());
        Base64.setEncoder(Java7Base64Encoder.getInstance());
        Base64UrlSafeEncoder.setEncoder(Java7Base64UrlSafeEncoder.getInstance());
        return null;
    }

}
