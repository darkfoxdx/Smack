/**
 *
 * Copyright 2014 Vyacheslav Blinov
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
package com.advisoryapps.smackx.amp.packet;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.advisoryapps.smack.packet.ExtensionElement;

import com.advisoryapps.smackx.amp.AMPDeliverCondition;
import com.advisoryapps.smackx.amp.AMPExpireAtCondition;
import com.advisoryapps.smackx.amp.AMPMatchResourceCondition;

public class AMPExtension implements ExtensionElement {

    public static final String NAMESPACE = "http://jabber.org/protocol/amp";
    public static final String ELEMENT = "amp";

    private final CopyOnWriteArrayList<Rule> rules = new CopyOnWriteArrayList<>();
    private boolean perHop = false;

    private final String from;
    private final String to;
    private final Status status;

    /**
     * Create a new AMPExtension instance with defined from, to and status attributes. Used to create incoming packets.
     * @param from jid that triggered this amp callback.
     * @param to receiver of this amp receipt.
     * @param status status of this amp receipt.
     */
    public AMPExtension(String from, String to, Status status) {
        this.from = from;
        this.to = to;
        this.status = status;
    }

    /**
     * Create a new amp request extension to be used with outgoing message.
     */
    public AMPExtension() {
        this.from = null;
        this.to = null;
        this.status = null;
    }

    /**
     * Get the JID that triggered this AMP callback.
     * @return jid that triggered this amp callback.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Get the receiver of this AMP receipt.
     * @return receiver of this amp receipt.
     */
    public String getTo() {
        return to;
    }

    /**
     * Status of this amp notification.
     * @return Status for this amp
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns a unmodifiable List of the rules in the packet.
     *
     * @return a unmodifiable List of the rules in the packet.
     */
    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    /**
     * Adds a rule to the amp element. Amp can have any number of rules.
     *
     * @param rule the rule to add.
     */
    public void addRule(Rule rule) {
        rules.add(rule);
    }

    /**
     * Returns a count of the rules in the AMP packet.
     *
     * @return the number of rules in the AMP packet.
     */
    public int getRulesCount() {
        return rules.size();
    }

    /**
     * Sets this amp ruleset to be "per-hop".
     *
     * @param enabled true if "per-hop" should be enabled
     */
    public synchronized void setPerHop(boolean enabled) {
        perHop = enabled;
    }

    /**
     * Returns true is this ruleset is "per-hop".
     *
     * @return true is this ruleset is "per-hop".
     */
    public synchronized boolean isPerHop() {
        return perHop;
    }

    /**
     * Returns the XML element name of the extension sub-packet root element.
     * Always returns "amp"
     *
     * @return the XML element name of the stanza extension.
     */
    @Override
    public String getElementName() {
        return ELEMENT;
    }

    /**
     * Returns the XML namespace of the extension sub-packet root element.
     * According the specification the namespace is always "http://jabber.org/protocol/xhtml-im"
     *
     * @return the XML namespace of the stanza extension.
     */
    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    /**
     * Returns the XML representation of a XHTML extension according the specification.
     **/
    @Override
    public String toXML(com.advisoryapps.smack.packet.XmlEnvironment enclosingNamespace) {
        StringBuilder buf = new StringBuilder();
        buf.append('<').append(getElementName()).append(" xmlns=\"").append(getNamespace()).append('"');
        if (status != null) {
            buf.append(" status=\"").append(status.toString()).append('"');
        }
        if (to != null) {
            buf.append(" to=\"").append(to).append('"');
        }
        if (from != null) {
            buf.append(" from=\"").append(from).append('"');
        }
        if (perHop) {
            buf.append(" per-hop=\"true\"");
        }
        buf.append('>');

        // Loop through all the rules and append them to the string buffer
        for (Rule rule : getRules()) {
            buf.append(rule.toXML());
        }

        buf.append("</").append(getElementName()).append('>');
        return buf.toString();
    }

    /**
     * XEP-0079 Rule element. Defines AMP Rule parameters. Can be added to AMPExtension.
     */
    public static class Rule {
        public static final String ELEMENT = "rule";

        private final Action action;
        private final Condition condition;

        public Action getAction() {
            return action;
        }

        public Condition getCondition() {
            return condition;
        }

        /**
         * Create a new amp rule with specified action and condition. Value will be taken from condition argument
         * @param action action for this rule
         * @param condition condition for this rule
         */
        public Rule(Action action, Condition condition) {
            if (action == null)
                throw new NullPointerException("Can't create Rule with null action");
            if (condition == null)
                throw new NullPointerException("Can't create Rule with null condition");

            this.action = action;
            this.condition = condition;
        }

        private String toXML() {
            return "<" + ELEMENT + " " + Action.ATTRIBUTE_NAME + "=\"" + action.toString() + "\" " +
                    Condition.ATTRIBUTE_NAME + "=\"" + condition.getName() + "\" " +
                    "value=\"" + condition.getValue() + "\"/>";
        }
    }

    /**
     * Interface for defining XEP-0079 Conditions and their values.
     * @see AMPDeliverCondition
     * @see AMPExpireAtCondition
     * @see AMPMatchResourceCondition
     **/
    public interface Condition {
        String getName();
        String getValue();

        String ATTRIBUTE_NAME = "condition";
    }

    /**
     * amp action attribute.
     * See http://xmpp.org/extensions/xep-0079.html#actions-def
     **/
    public enum Action {
        /**
         * The "alert" action triggers a reply &lt;message/&gt; stanza to the sending entity.
         * This &lt;message/&gt; stanza MUST contain the element &lt;amp status='alert'/&gt;,
         * which itself contains the &lt;rule/&gt; that triggered this action. In all other respects,
         * this action behaves as "drop".
         */
        alert,
        /**
         * The "drop" action silently discards the message from any further delivery attempts
         * and ensures that it is not placed into offline storage.
         * The drop MUST NOT result in other responses.
         */
        drop,
        /**
         * The "error" action triggers a reply &lt;message/&gt; stanza of type "error" to the sending entity.
         * The &lt;message/&gt; stanza's &lt;error/&gt; child MUST contain a
         * &lt;failed-rules xmlns='http://jabber.org/protocol/amp#errors'/&gt; error condition,
         * which itself contains the rules that triggered this action.
         */
        error,
        /**
         * The "notify" action triggers a reply &lt;message/&gt; stanza to the sending entity.
         * This &lt;message/&gt; stanza MUST contain the element &lt;amp status='notify'/&gt;, which itself
         * contains the &lt;rule/&gt; that triggered this action. Unlike the other actions,
         * this action does not override the default behavior for a server.
         * Instead, the server then executes its default behavior after sending the notify.
         */
        notify;

        public static final String ATTRIBUTE_NAME = "action";
    }

    /**
     * amp notification status as defined by XEP-0079.
     */
    public enum Status {
        alert,
        error,
        notify
    }
}
