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
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbSecurityType;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 * This is the security handshake, phase 2. Sent by the client to the server
 * (S2C) after the server tells of its supported security types: the client must
 * decide which type to use. Described in RFC 6143, paragraph 7.1.2
 *
 * @author dmerkushov
 */
public class SecurityHandshake2_C2S extends RfbMessage {

	int secTypeInt;
	RfbSecurityType secType;

	public SecurityHandshake2_C2S (RfbSession session) {
		super (session);
	}

	public SecurityHandshake2_C2S (RfbSession session, RfbSecurityType secType) {
		super (session);

		Objects.requireNonNull (secType);

		this.secType = secType;
	}

	@Override
	public void write (OutputStream out) throws IOException {
		Objects.requireNonNull (out);

		out.write (secTypeInt);
	}

	@Override
	public void read (InputStream in) throws IOException {
		Objects.requireNonNull (in);

		secTypeInt = in.read ();

		secType = RfbSecurityType.getSecTypeByValue (secTypeInt);
	}

}
