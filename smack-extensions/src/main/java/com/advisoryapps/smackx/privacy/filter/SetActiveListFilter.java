/**
 *
 * Copyright 2015 Florian Schmaus
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
package com.advisoryapps.smackx.privacy.filter;

import com.advisoryapps.smack.filter.FlexibleStanzaTypeFilter;
import com.advisoryapps.smack.packet.IQ.Type;

import com.advisoryapps.smackx.privacy.packet.Privacy;

public final class SetActiveListFilter extends FlexibleStanzaTypeFilter<Privacy> {

    public static final SetActiveListFilter INSTANCE = new SetActiveListFilter();

    private SetActiveListFilter() {
    }

    @Override
    protected boolean acceptSpecific(Privacy privacy) {
        if (privacy.getType() != Type.set) {
            return false;
        }
        return privacy.getActiveName() != null || privacy.isDeclineActiveList();
    }

}
