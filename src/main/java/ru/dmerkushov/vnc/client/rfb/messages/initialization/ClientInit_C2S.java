/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.initialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readBoolean;
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.writeBoolean;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 * This is the ClientInit message, a message sent by the client to the server
 * (C2S) after the client validates against the security considerations.
 * Described in RFC 6143, paragraph 7.3.1
 *
 * Before this one is:
 * {@link ru.dmerkushov.vnc.client.rfb.messages.handshake.SecurityResultHandshake_S2C}
 *
 * After this one is:
 * {@link ru.dmerkushov.vnc.client.rfb.messages.initialization.ServerInit_S2C}
 *
 * @author dmerkushov
 */
public class ClientInit_C2S extends RfbMessage {

	private boolean sharedDesktop;

	public ClientInit_C2S (RfbClientSession session) {
		super (session);
	}

	public ClientInit_C2S (RfbClientSession session, boolean sharedDesktop) {
		this (session);

		this.sharedDesktop = sharedDesktop;
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		writeBoolean (out, sharedDesktop);
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		sharedDesktop = readBoolean (in);
	}

}
