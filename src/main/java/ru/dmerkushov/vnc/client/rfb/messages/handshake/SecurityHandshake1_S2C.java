/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.rfb.messages.handshake;

import ru.dmerkushov.vnc.client.VncCommon;
import ru.dmerkushov.vnc.client.rfb.messages.MessageException;
import ru.dmerkushov.vnc.client.rfb.messages.RfbMessage;
import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;
import ru.dmerkushov.vnc.client.rfb.session.RfbSecurityType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.logging.Level;

import static ru.dmerkushov.vnc.client.rfb.messages.util.RfbMessagesUtil.readString;

/**
 * This is the security handshake, phase 1. Sent by the server to the client (S2C) after the version of the protocol is
 * decided (via {@link ProtocolVersionHandshake}). Described in RFC 6143, paragraph 7.1.2
 *
 * @author dmerkushov
 */
public class SecurityHandshake1_S2C extends RfbMessage {

	private int secTypeCount;
	private int[] secTypesInt;
	private LinkedHashSet<RfbSecurityType> secTypes;

	public SecurityHandshake1_S2C (RfbClientSession session) {
		super (session);
	}

	@Override
	public void write (OutputStream out) throws MessageException, IOException {
		Objects.requireNonNull (out);

		out.write (this.secTypeCount);

		for (int secType : this.secTypesInt) {
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

		this.secTypeCount = in.read ();

		this.secTypes = new LinkedHashSet<> ();

		switch (this.secTypeCount) {
			case 0:
				String reason = readString (in);

				throw new MessageException ("Security handshake 1 failed. Reason supplied by the server is: " + reason + "(reason length " + reason.length () + ")");
			case 1:
				this.secTypesInt = new int[1];
				this.secTypesInt[0] = in.read ();
				this.secTypes.add (RfbSecurityType.getSecTypeByValue (this.secTypesInt[0]));
				break;
			default:
				this.secTypesInt = new int[this.secTypeCount];
				for (int i = 0; i < this.secTypeCount; i++) {
					try {
						this.secTypesInt[i] = in.read ();
					} catch (IOException ex) {
						throw new IOException ("When reading secType #" + (i), ex);
					}

					RfbSecurityType secType = RfbSecurityType.getSecTypeByValue (this.secTypesInt[i]);

					if (secType == null) {
						VncCommon.getLogger ().log (Level.WARNING, "Unsupported security type: {0}, probably Tight(16) or RealVNC(3-4,7-15,128-255)?", this.secTypesInt[i]);
					} else {
						VncCommon.getLogger ().log (Level.INFO, "Adding secType {0} - {1}", new Object[]{this.secTypesInt[i], secType.name ()});
						this.secTypes.add (secType);
					}
				}
				break;
		}
	}

	public LinkedHashSet<RfbSecurityType> getSecurityTypes () {
		return this.secTypes;
	}

}
