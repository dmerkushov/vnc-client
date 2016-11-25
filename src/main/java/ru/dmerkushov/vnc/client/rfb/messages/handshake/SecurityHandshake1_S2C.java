/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.handshake;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Objects;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbSecurityType;
import ru.dmerkushov.vnc.client.rfb.session.RfbSession;

/**
 * This is the security handshake, phase 1. Sent by the server to the client
 * (S2C) after the version of the protocol is decided (via
 * {@link ProtocolVersionHandshake}). Described in RFC 6143, paragraph 7.1.2
 *
 * @author dmerkushov
 */
public class SecurityHandshake1_S2C extends RfbMessage {

	int secTypeCount;
	int[] secTypesInt;
	LinkedHashSet<RfbSecurityType> secTypes;

	public SecurityHandshake1_S2C (RfbSession session) {
		super (session);
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		Objects.requireNonNull (out);

		out.write (secTypeCount);

		for (int secType : secTypesInt) {
			try {
				out.write (secType);
			} catch (IOException ex) {
				throw new IOException ("When writing secType " + secType, ex);
			}
		}
	}

	@Override
	public void read (InputStream in) throws MessageException, IOException {
		Objects.requireNonNull (in);

		secTypeCount = in.read ();

		secTypes = new LinkedHashSet<> ();

		if (secTypeCount == 1) {
			secTypesInt = new int[1];
			secTypesInt[0] = in.read ();
			secTypes.add (RfbSecurityType.getSecTypeByValue (secTypesInt[0]));
		} else {
			secTypesInt = new int[secTypeCount];
			for (int i = 1; i < secTypeCount; i++) {
				try {
					secTypesInt[i - 1] = in.read ();
				} catch (IOException ex) {
					throw new IOException ("When reading secType #" + (i - 1), ex);
				}
				secTypes.add (RfbSecurityType.getSecTypeByValue (secTypesInt[i - 1]));
			}
		}

		//TODO Implement support for error in S2C security handshake 1 ("If number-of-security-types is zero..."), page 9 of the protocol spec
	}

	public LinkedHashSet<RfbSecurityType> getSecurityTypes () {
		return secTypes;
	}

}
