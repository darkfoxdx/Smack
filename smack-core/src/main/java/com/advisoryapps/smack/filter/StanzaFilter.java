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

package com.advisoryapps.smack.filter;

import com.advisoryapps.smack.packet.Stanza;

/**
 * Defines a way to filter stanzas for particular attributes. Stanza filters are used when
 * constructing stanza listeners or collectors -- the filter defines what stanzas match the criteria
 * of the collector or listener for further stanza processing.
 * <p>
 * Several simple filters are pre-defined. These filters can be logically combined for more complex
 * stanza filtering by using the {@link com.advisoryapps.smack.filter.AndFilter AndFilter} and
 * {@link com.advisoryapps.smack.filter.OrFilter OrFilter} filters. It's also possible to define
 * your own filters by implementing this interface. The code example below creates a trivial filter
 * for stanzas with a specific ID (real code should use {@link StanzaIdFilter} instead).
 *
 * <pre>
 * // Use an anonymous inner class to define a stanza filter that returns
 * // all stanzas that have a stanza ID of &quot;RS145&quot;.
 * StanzaFilter myFilter = new StanzaFilter() {
 *     public boolean accept(Stanza stanza) {
 *         return &quot;RS145&quot;.equals(stanza.getStanzaId());
 *     }
 * };
 * // Create a new stanza collector using the filter we created.
 * StanzaCollector myCollector = connection.createStanzaCollector(myFilter);
 * </pre>
 * <p>
 * As a rule of thumb: If you have a predicate method, that is, a method which takes a single Stanza as argument, is pure
 * (side effect free) and returns only a boolean, then it is a good indicator that the logic should be put into a
 * {@link StanzaFilter} (and be referenced in {@link com.advisoryapps.smack.StanzaListener}).
 * </p>
 *
 * @see com.advisoryapps.smack.StanzaCollector
 * @see com.advisoryapps.smack.StanzaListener
 * @author Matt Tucker
 */
public interface StanzaFilter {

    /**
     * Tests whether or not the specified stanza should pass the filter.
     *
     * @param stanza the stanza to test.
     * @return true if and only if <code>stanza</code> passes the filter.
     */
    boolean accept(Stanza stanza);
}
