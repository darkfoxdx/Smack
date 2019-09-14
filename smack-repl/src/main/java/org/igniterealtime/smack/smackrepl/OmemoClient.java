/**
 *
 * Copyright 2019 Paul Schaub
 *
 * This file is part of smack-repl.
 *
 * smack-repl is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
package org.igniterealtime.smack.smackrepl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.advisoryapps.smack.SmackConfiguration;
import com.advisoryapps.smack.SmackException;
import com.advisoryapps.smack.SmackException.NotConnectedException;
import com.advisoryapps.smack.SmackException.NotLoggedInException;
import com.advisoryapps.smack.XMPPException;
import com.advisoryapps.smack.packet.Message;
import com.advisoryapps.smack.packet.Stanza;
import com.advisoryapps.smack.tcp.XMPPTCPConnection;
import com.advisoryapps.smack.tcp.XMPPTCPConnectionConfiguration;

import com.advisoryapps.smackx.carbons.packet.CarbonExtension;
import com.advisoryapps.smackx.muc.MultiUserChat;
import com.advisoryapps.smackx.omemo.OmemoManager;
import com.advisoryapps.smackx.omemo.OmemoMessage;
import com.advisoryapps.smackx.omemo.exceptions.CannotEstablishOmemoSessionException;
import com.advisoryapps.smackx.omemo.exceptions.CorruptedOmemoKeyException;
import com.advisoryapps.smackx.omemo.exceptions.CryptoFailedException;
import com.advisoryapps.smackx.omemo.exceptions.UndecidedOmemoIdentityException;
import com.advisoryapps.smackx.omemo.internal.OmemoDevice;
import com.advisoryapps.smackx.omemo.listener.OmemoMessageListener;
import com.advisoryapps.smackx.omemo.listener.OmemoMucMessageListener;
import com.advisoryapps.smackx.omemo.signal.SignalCachingOmemoStore;
import com.advisoryapps.smackx.omemo.signal.SignalFileBasedOmemoStore;
import com.advisoryapps.smackx.omemo.signal.SignalOmemoService;
import com.advisoryapps.smackx.omemo.trust.OmemoFingerprint;
import com.advisoryapps.smackx.omemo.trust.OmemoTrustCallback;
import com.advisoryapps.smackx.omemo.trust.TrustState;
import com.advisoryapps.smackx.pubsub.PubSubException;

import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

public class OmemoClient {

    public static final Logger LOGGER = Logger.getLogger(OmemoClient.class.getName());

    private static final Scanner scanner = new Scanner(System.in, "UTF-8");
    private final XMPPTCPConnection connection;
    private final OmemoManager omemoManager;

    public static void main(String[] args)
            throws XMPPException, SmackException, IOException, InterruptedException, CorruptedOmemoKeyException {
        SmackConfiguration.DEBUG = true;
        if (args.length != 2) {
            print("Missing arguments: <jid> <password>");
            return;
        }
        SignalOmemoService.acknowledgeLicense();
        SignalOmemoService.setup();
        SignalOmemoService omemoService = (SignalOmemoService) SignalOmemoService.getInstance();
        Path omemoStoreDirectory = Files.createTempDirectory("omemo-store");
        omemoService.setOmemoStoreBackend(new SignalCachingOmemoStore(new SignalFileBasedOmemoStore(omemoStoreDirectory.toFile())));

        EntityBareJid jid = JidCreate.entityBareFromOrThrowUnchecked(args[0]);
        String password = args[1];
        OmemoClient client = new OmemoClient(jid, password);
        try {
            client.start();

            while (true) {
                String input = scanner.nextLine();
                if (input.startsWith("/quit")) {
                    break;
                }
                if (input.isEmpty()) {
                    continue;
                }
                client.handleInput(input);
            }
        } finally {
            client.stop();
        }
    }

    public OmemoClient(EntityBareJid jid, String password) {
        connection = new XMPPTCPConnection(XMPPTCPConnectionConfiguration.builder()
                .setXmppAddressAndPassword(jid, password).build());
        connection.setReplyTimeout(10 * 1000);
        omemoManager = OmemoManager.getInstanceFor(connection);
        omemoManager.setTrustCallback(new OmemoTrustCallback() {
            // In a real app you'd want to persist these decisions
            private final Map<OmemoFingerprint, TrustState> trustStateMap = new HashMap<>();
            @Override
            public TrustState getTrust(OmemoDevice device, OmemoFingerprint fingerprint) {
                return trustStateMap.get(fingerprint) != null ? trustStateMap.get(fingerprint) : TrustState.undecided;
            }

            @Override
            public void setTrust(OmemoDevice device, OmemoFingerprint fingerprint, TrustState state) {
                trustStateMap.put(fingerprint, state);
            }
        });
        omemoManager.addOmemoMessageListener(new OmemoMessageListener() {
            @Override
            public void onOmemoMessageReceived(Stanza s, OmemoMessage.Received m) {
                print(m.getSenderDevice() + ": " + (m.getBody() != null ? m.getBody() : "<keyTransportMessage>"));
            }

            @Override
            public void onOmemoCarbonCopyReceived(CarbonExtension.Direction d, Message cc, Message wm, OmemoMessage.Received m) {
                onOmemoMessageReceived(cc, m);
            }
        });
        omemoManager.addOmemoMucMessageListener(new OmemoMucMessageListener() {
            @Override
            public void onOmemoMucMessageReceived(MultiUserChat muc, Stanza s, OmemoMessage.Received m) {
                print(s.getFrom() + ":" + m.getSenderDevice().getDeviceId() + ": " + (m.getBody() != null ? m.getBody() : "<keyTransportMessage>"));
            }
        });
    }

    public void start()
            throws XMPPException, SmackException, IOException, InterruptedException, CorruptedOmemoKeyException {
        connection.connect().login();
        omemoManager.initialize();
        print("Logged in!");
    }

    public void stop() {
        connection.disconnect();
    }

    public void handleInput(String input)
            throws NotConnectedException, NotLoggedInException, InterruptedException, IOException {
        String[] com = input.split(" ", 3);
        switch (com[0]) {
            case "/omemo":
                if (com.length < 3) {
                    print("Usage: /omemo <contact-jid> <message>");
                    return;
                }

                BareJid recipient = JidCreate.bareFrom(com[1]);
                String body = com[2];

                try {
                    Message omemoMessage = omemoManager.encrypt(recipient, body).asMessage(recipient);
                    connection.sendStanza(omemoMessage);
                } catch (UndecidedOmemoIdentityException e) {
                    print("Undecided Identities!\n" + Arrays.toString(e.getUndecidedDevices().toArray()));
                } catch (CryptoFailedException | SmackException.NoResponseException e) {
                    LOGGER.log(Level.SEVERE, "Unexpected Exception", e);
                }
                break;
            case "/trust":
                print("Trust");
                if (com.length != 2) {
                    print("Usage: /trust <contact-jid>");
                }

                BareJid contact = JidCreate.bareFrom(com[1]);

                HashMap<OmemoDevice, OmemoFingerprint> devices;
                try {
                    devices = omemoManager.getActiveFingerprints(contact);
                } catch (CorruptedOmemoKeyException | CannotEstablishOmemoSessionException | SmackException.NoResponseException e) {
                    LOGGER.log(Level.SEVERE, "Unexpected Exception", e);
                    return;
                }
                for (OmemoDevice d : devices.keySet()) {
                    print("Trust (1) or distrust (2)?\n" + devices.get(d).blocksOf8Chars());
                    if (Integer.parseInt(scanner.nextLine()) == 1) {
                        omemoManager.trustOmemoIdentity(d, devices.get(d));
                    } else {
                        omemoManager.distrustOmemoIdentity(d, devices.get(d));
                    }
                }
                print("Done.");
                break;
            case "/purge":
                try {
                    omemoManager.purgeDeviceList();
                    print("Purged.");
                } catch (XMPPException.XMPPErrorException | SmackException.NoResponseException | PubSubException.NotALeafNodeException e) {
                    LOGGER.log(Level.SEVERE, "Unexpected Exception", e);
                }
        }
    }

    private static void print(String msg) {
        // CHECKSTYLE:OFF
        System.out.println(msg);
        // CHECKSTYLE:ON
    }
}
