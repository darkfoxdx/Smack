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
package com.advisoryapps.smack.filter;

import com.advisoryapps.smack.XMPPConnection;
import com.advisoryapps.smack.packet.IQ;
import com.advisoryapps.smack.packet.Stanza;

public class IQResultReplyFilter extends IQReplyFilter {


    public IQResultReplyFilter(IQ iqPacket, XMPPConnection conn) {
        super(iqPacket, conn);
    }

    @Override
    public boolean accept(Stanza packet) {
        if (!super.accept(packet)) {
            return false;
        }
        return IQTypeFilter.RESULT.accept(packet);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" (" + super.toString() + ')');
        return sb.toString();
    }
}
