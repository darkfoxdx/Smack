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

package com.advisoryapps.smackx.muc;

import org.jxmpp.jid.EntityFullJid;

/**
 * A listener that is fired anytime a MUC room changes its subject.
 *
 * @author Gaston Dombiak
 */
public interface SubjectUpdatedListener {

    /**
     * Called when a MUC room has changed its subject.
     *
     * @param subject the new room's subject.
     * @param from the user that changed the room's subject or <code>null</code> if the room itself changed the subject.
     */
    void subjectUpdated(String subject, EntityFullJid from);

}
