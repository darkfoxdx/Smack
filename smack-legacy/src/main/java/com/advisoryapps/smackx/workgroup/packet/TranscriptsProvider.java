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

package com.advisoryapps.smackx.workgroup.packet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.advisoryapps.smack.packet.XmlEnvironment;
import com.advisoryapps.smack.provider.IQProvider;
import com.advisoryapps.smack.util.ParserUtils;
import com.advisoryapps.smack.xml.XmlPullParser;
import com.advisoryapps.smack.xml.XmlPullParserException;

import org.jxmpp.jid.Jid;

/**
 * An IQProvider for transcripts summaries.
 *
 * @author Gaston Dombiak
 */
public class TranscriptsProvider extends IQProvider<Transcripts> {

    @SuppressWarnings("DateFormatConstant")
    private static final SimpleDateFormat UTC_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
    static {
        UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

    @Override
    public Transcripts parse(XmlPullParser parser, int initialDepth, XmlEnvironment xmlEnvironment) throws XmlPullParserException, IOException {
        Jid userID = ParserUtils.getJidAttribute(parser, "userID");
        List<Transcripts.TranscriptSummary> summaries = new ArrayList<>();

        boolean done = false;
        while (!done) {
            XmlPullParser.Event eventType = parser.next();
            if (eventType == XmlPullParser.Event.START_ELEMENT) {
                if (parser.getName().equals("transcript")) {
                    summaries.add(parseSummary(parser));
                }
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getName().equals("transcripts")) {
                    done = true;
                }
            }
        }

        return new Transcripts(userID, summaries);
    }

    private static Transcripts.TranscriptSummary parseSummary(XmlPullParser parser)
                    throws IOException, XmlPullParserException {
        String sessionID =  parser.getAttributeValue("", "sessionID");
        Date joinTime = null;
        Date leftTime = null;
        List<Transcripts.AgentDetail> agents = new ArrayList<>();

        boolean done = false;
        while (!done) {
            XmlPullParser.Event eventType = parser.next();
            if (eventType == XmlPullParser.Event.START_ELEMENT) {
                if (parser.getName().equals("joinTime")) {
                    try {
                        synchronized (UTC_FORMAT) {
                            joinTime = UTC_FORMAT.parse(parser.nextText());
                        }
                    } catch (ParseException e) { }
                }
                else if (parser.getName().equals("leftTime")) {
                    try {
                        synchronized (UTC_FORMAT) {
                            leftTime = UTC_FORMAT.parse(parser.nextText());
                        }
                    } catch (ParseException e) { }
                }
                else if (parser.getName().equals("agents")) {
                    agents = parseAgents(parser);
                }
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getName().equals("transcript")) {
                    done = true;
                }
            }
        }

        return new Transcripts.TranscriptSummary(sessionID, joinTime, leftTime, agents);
    }

    private static List<Transcripts.AgentDetail> parseAgents(XmlPullParser parser)
                    throws IOException, XmlPullParserException {
        List<Transcripts.AgentDetail> agents = new ArrayList<>();
        String agentJID =  null;
        Date joinTime = null;
        Date leftTime = null;

        boolean done = false;
        while (!done) {
            XmlPullParser.Event eventType = parser.next();
            if (eventType == XmlPullParser.Event.START_ELEMENT) {
                if (parser.getName().equals("agentJID")) {
                    agentJID = parser.nextText();
                }
                else if (parser.getName().equals("joinTime")) {
                    try {
                        synchronized (UTC_FORMAT) {
                            joinTime = UTC_FORMAT.parse(parser.nextText());
                        }
                    } catch (ParseException e) { }
                }
                else if (parser.getName().equals("leftTime")) {
                    try {
                        synchronized (UTC_FORMAT) {
                            leftTime = UTC_FORMAT.parse(parser.nextText());
                        }
                    } catch (ParseException e) { }
                }
                else if (parser.getName().equals("agent")) {
                    agentJID =  null;
                    joinTime = null;
                    leftTime = null;
                }
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT) {
                if (parser.getName().equals("agents")) {
                    done = true;
                }
                else if (parser.getName().equals("agent")) {
                    agents.add(new Transcripts.AgentDetail(agentJID, joinTime, leftTime));
                }
            }
        }
        return agents;
    }
}
