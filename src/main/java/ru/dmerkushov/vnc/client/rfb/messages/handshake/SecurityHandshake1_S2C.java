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
import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readString;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbSecurityType;

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

	public SecurityHandshake1_S2C (RfbClientSession session) {
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

		switch (secTypeCount) {
			case 0:
				String reason = readString (in);

				throw new MessageException ("Security handshake 1 failed. Reason supplied by the server is: " + reason + "(reason length " + reason.length () + ")");
			case 1:
				secTypesInt = new int[1];
				secTypesInt[0] = in.read ();
				secTypes.add (RfbSecurityType.getSecTypeByValue (secTypesInt[0]));
				break;
			default:
				secTypesInt = new int[secTypeCount];
				for (int i = 0; i < secTypeCount; i++) {
					try {
						secTypesInt[i] = in.read ();
					} catch (IOException ex) {
						throw new IOException ("When reading secType #" + (i), ex);
					}

					RfbSecurityType secType = RfbSecurityType.getSecTypeByValue (secTypesInt[i]);

					if (secType == null) {
						System.err.println ("Unknown security type: " + secTypesInt[i]);
					} else {
						System.out.println ("Adding secType " + secTypesInt[i] + " - " + secType.name ());
						secTypes.add (secType);
					}
				}
				break;
		}
	}

	public LinkedHashSet<RfbSecurityType> getSecurityTypes () {
		return secTypes;
	}

}
