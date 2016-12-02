/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.handshake;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 * This is the security handshake, phase 3, in case of VNC authentication. Sent
 * by the server to the client (S2C) if the client decides to use the VNC
 * authentication type. Described in RFC 6143, paragraph 7.2.2
 *
 * @author dmerkushov
 */
public class SecurityHandshake3_VNCauth_S2C extends RfbMessage {

	byte[] challenge;

	public static final int CHALLENGE_STD_LENGTH = 16;

	public SecurityHandshake3_VNCauth_S2C (RfbClientSession session) {
		super (session);
	}

	public SecurityHandshake3_VNCauth_S2C (RfbClientSession session, byte[] challenge) {
		this (session);

		Objects.requireNonNull (challenge);

		if (challenge.length != CHALLENGE_STD_LENGTH) {
			throw new IllegalArgumentException ("Challenge is not of standard length (" + CHALLENGE_STD_LENGTH + "): " + challenge.length);
		}

		this.challenge = challenge;
	}

	@Override
	public void write (OutputStream out) throws IOException {
		Objects.requireNonNull (out);

		out.write (challenge);
	}

	@Override
	public void read (InputStream in) throws IOException, MessageException {
		Objects.requireNonNull (in);

		int bytesRead = in.read (challenge);

		if (bytesRead != CHALLENGE_STD_LENGTH) {
			throw new MessageException ("Read not " + CHALLENGE_STD_LENGTH + " bytes as expected: " + bytesRead);
		}
	}

}
